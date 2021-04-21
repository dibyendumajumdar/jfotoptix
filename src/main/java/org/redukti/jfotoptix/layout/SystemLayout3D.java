package org.redukti.jfotoptix.layout;

import org.redukti.jfotoptix.math.*;
import org.redukti.jfotoptix.rendering.Renderer;
import org.redukti.jfotoptix.rendering.RendererSvg;
import org.redukti.jfotoptix.rendering.RendererViewport;
import org.redukti.jfotoptix.rendering.Rgb;
import org.redukti.jfotoptix.sys.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SystemLayout3D {

    /** Not sure if this works and is worthwile
     */
    public void layout3d(RendererSvg r, OpticalSystem system) {
        r.set_page (0);
        r.set_perspective ();

        draw_3d_fit (r, system, 300);
        draw_3d (r, system);
    }

    void draw_3d_fit(RendererViewport r, Container c, double z_offset) {
        Transform3 t = new Transform3();
        t = t.set_direction(Vector3.vector3_100.negate());
        t = t.translation_reset();

        Vector3Pair b = c.get_bounding_box();
        Vector3 b0 = new Vector3(b.v0.z(), b.v0.y(), b.v0.x());
        Vector3 b1 = new Vector3(b.v1.z(), b.v1.y(), b.v1.x());

        Vector3 center = b0.plus(b1).divide(2.0);
        double width
                = (center.x() - b1.x()) / Math.tan(Math.toRadians(r.get_fov()) / 2.0);
        double dist = width + (center.z() + b0.z()) + z_offset;

        t = t.set_translation(t.transform(center).plus(new Vector3(dist, 0, 0)));

        r.set_camera_transform(t.inverse());
        r.set_feature_size(Math.abs(width / 20.0));
    }

    void draw_3d(Renderer r, Container c) {
        if (c instanceof Element) {
            draw_element_3d(r, (Element) c, (Element) c);
            return;
        }
        for (Element i : c.elements()) {
            draw_element_3d(r, i, null);
        }
    }

    void draw_element_3d(Renderer r, Element e, Element ref_) {
        if (e instanceof Group) {
            r.group_begin("element");
            draw_3d_e(r, (Group) e, ref_);
            r.group_end();
        } else if (e instanceof Stop) {
            r.group_begin("element");
            draw_3d_e(r, (Stop) e, ref_);
            r.group_end();
        } else if (e instanceof Surface) {
            r.group_begin("element");
            draw_3d_e(r, (Surface) e, ref_);
            r.group_end();
        }
    }

    void draw_3d_e(Renderer r, Group g, Element ref_) {
        for (Element i : g.elements()) {
            draw_element_3d(r, i, ref_);
        }
    }

    void draw_3d_e(Renderer r, Surface surface, Element ref_) {
        Rgb color = r.get_style_color(Renderer.Style.StyleSurface);

        ArrayList<Triangle2> mesh = new ArrayList<>();
        surface.get_shape().get_triangles(
                (Triangle2 in) -> {
                    mesh.add(in);
                },
                r.get_feature_size());

        Transform3 tr = surface.get_global_transform();

        for (Triangle2 t : mesh) {
            Triangle3 pts = new Triangle3(Vector3.vector3_0, Vector3.vector3_0, Vector3.vector3_0);
            Triangle3 nrm = pts;

            for (int i = 0; i < 3; i++) {
                Vector3 v = new Vector3(t.v(i).x(), t.v(i).y(), surface.get_curve().sagitta(t.v(i)));
                pts = pts.v(i, v);

                nrm = nrm.v(i, surface.get_curve().normal(pts.v(i)));

                pts = pts.v(i, tr.transform(pts.v(i)));
                nrm = nrm.v(i, tr.apply_rotation(nrm.v(i)));
            }

            r.draw_triangle(pts, nrm, color);
        }
    }

    void draw_3d_e (Renderer r, Stop stop, Element ref_)
    {
        ArrayList<Vector3> poly = new ArrayList<>();

        Consumer<Vector2> d = (Vector2 t) -> {
            poly.add (stop.get_transform_to (ref_).transform (new Vector3 (t.x(), t.y(), 0.0)));
        };

        for (int i = 0; i < stop.get_shape ().get_contour_count (); i++)
        {
            stop.get_shape ().get_contour (i, d, r.get_feature_size ());
            r.draw_polygon (poly.toArray(new Vector3[poly.size()]),
                    r.get_style_color(Renderer.Style.StyleSurface),
                    false, true);
            poly.clear ();
        }
    }
}
