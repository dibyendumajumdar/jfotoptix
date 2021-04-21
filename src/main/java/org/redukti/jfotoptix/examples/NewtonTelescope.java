/*
The software is ported from Goptical, hence is licensed under the GPL.
Copyright (c) 2021 Dibyendu Majumdar

Original GNU Optical License and Authors are as follows:

      The Goptical library is free software; you can redistribute it
      and/or modify it under the terms of the GNU General Public
      License as published by the Free Software Foundation; either
      version 3 of the License, or (at your option) any later version.

      The Goptical library is distributed in the hope that it will be
      useful, but WITHOUT ANY WARRANTY; without even the implied
      warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
      See the GNU General Public License for more details.

      You should have received a copy of the GNU General Public
      License along with the Goptical library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307 USA

      Copyright (C) 2010-2011 Free Software Foundation, Inc
      Author: Alexandre Becoulet
 */


package org.redukti.jfotoptix.examples;

import org.redukti.jfotoptix.curve.Conic;
import org.redukti.jfotoptix.curve.Flat;
import org.redukti.jfotoptix.layout.SystemLayout2D;
import org.redukti.jfotoptix.layout.SystemLayout3D;
import org.redukti.jfotoptix.light.SpectralLine;
import org.redukti.jfotoptix.material.Sellmeier;
import org.redukti.jfotoptix.math.MathUtils;
import org.redukti.jfotoptix.math.Transform3;
import org.redukti.jfotoptix.math.Vector3;
import org.redukti.jfotoptix.math.Vector3Pair;
import org.redukti.jfotoptix.rendering.RendererSvg;
import org.redukti.jfotoptix.rendering.RendererViewport;
import org.redukti.jfotoptix.shape.Disk;
import org.redukti.jfotoptix.shape.Ellipse;
import org.redukti.jfotoptix.shape.Rectangle;
import org.redukti.jfotoptix.sys.*;

import java.util.ArrayList;
import java.util.List;

public class NewtonTelescope {

    static class Newton extends Group
    {
        final double _focal;
        final double _diameter;
        final double _bwd;
        final double _field_angle;
        final double _unvignetted_image_size;
        final double _offset;
        final double _minor_axis;
        final double _major_axis;

        final Disk _primary_shape;
        final Conic _primary_curve;
        final MirrorSurface _primary;
        final Ellipse _secondary_shape;
        final MirrorSurface _secondary;
        final Vector3Pair _focal_plane;

        public Newton(int id, Vector3Pair p, Transform3 transform3, List<? extends Element> elements,
                      double _focal, double _diameter, double _bwd, double _field_angle, double _unvignetted_image_size,
                      double _offset, double _minor_axis, double _major_axis, Disk _primary_shape,
                      Conic _primary_curve, MirrorSurface _primary, Ellipse _secondary_shape,
                      MirrorSurface _secondary, Vector3Pair _focal_plane) {
            super(id, p, transform3, elements);
            this._focal = _focal;
            this._diameter = _diameter;
            this._bwd = _bwd;
            this._field_angle = _field_angle;
            this._unvignetted_image_size = _unvignetted_image_size;
            this._offset = _offset;
            this._minor_axis = _minor_axis;
            this._major_axis = _major_axis;
            this._primary_shape = _primary_shape;
            this._primary_curve = _primary_curve;
            this._primary = _primary;
            this._secondary_shape = _secondary_shape;
            this._secondary = _secondary;
            this._focal_plane = _focal_plane;
        }

        public static class Builder extends Group.Builder {

            double _focal;
            double _diameter;
            double _bwd;
            double _field_angle;
            double _unvignetted_image_size;
            double _offset;
            double _minor_axis;
            double _major_axis;

            Disk _primary_shape;
            Conic _primary_curve;
            MirrorSurface.Builder _primary;
            Ellipse _secondary_shape;
            MirrorSurface.Builder _secondary;
            Vector3Pair _focal_plane;


            @Override
            public Newton build() {
                return new Newton(id, position, transform, getElements(),
                        _focal, _diameter, _bwd, _field_angle, _unvignetted_image_size,
                        _offset, _minor_axis, _major_axis, _primary_shape,
                        _primary_curve, _primary.build(), _secondary_shape,
                        _secondary.build(), _focal_plane);
            }

            public Builder(Vector3Pair p, double focal, double diameter,
                                double bwd, double field_angle) {
                position(p);
                _focal = focal;
                _diameter = diameter;
                _bwd = (_diameter / 2.0 + bwd);
                _field_angle = (field_angle);
                _unvignetted_image_size = calc_unvignetted_image_size ();
                _offset = calc_secondary ();
                _focal_plane = new Vector3Pair(new Vector3 (0, _bwd, 0), new Vector3 (0, 1.0, 0));
                _primary_shape = new Disk (diameter / 2.0);
                _primary_curve = new Conic (_focal * 2.0, -1.0);
                _primary = new MirrorSurface.Builder (false)
                        .position(new Vector3Pair (new Vector3(0, 0, _focal - _bwd),
                                new Vector3(0, 0, -1.0)))
                        .curve(_primary_curve)
                        .shape(_primary_shape);
                _secondary_shape = new Ellipse (_minor_axis / 2.0,
                        _major_axis / 2.0);
                _secondary = new MirrorSurface.Builder (true)
                        .position(new Vector3Pair(new Vector3 (0, -_offset, _offset), Vector3.vector3_001))
                        .curve(Flat.flat)
                        .shape(_secondary_shape);
                _secondary.transform(_secondary.transform().rotate_axis_by_angles(new Vector3(-135, 0, 0)));
                add (_primary);
                add (_secondary);
            }

            double calc_unvignetted_image_size ()
            {
                return _unvignetted_image_size
                        = Math.tan (Math.toRadians (_field_angle / 2.0)) * _focal * 2;
            }

            double calc_secondary ()
            {
                // formula from http://www.astro-electronic.de/faq2.html

                double e = MathUtils.square (_diameter) / (16.0 * _focal);
                double c = _focal - e;
                double b = _diameter - _unvignetted_image_size;
                double l = _unvignetted_image_size * c + _bwd * b;
                double m = 2.0 * c - b;
                double n = 2.0 * c + b;
                double a = l / m + l / n;
                double o = (l / m - l / n) / 2.0;

                _offset = o;
                _minor_axis = Math.sqrt (MathUtils.square (a) - 4.0 * MathUtils.square (_offset));
                _major_axis = Math.sqrt (2.0) * a;

                return _offset;
            }

            Vector3Pair get_focal_plane() {
                return _focal_plane;
            }
        }
    }

    static Vector3Pair get_exit_plane (Lens.Builder lens)
    {
        return lens.transform ().transform_line (
                new Vector3Pair (new Vector3(0., 0., lens.zoffset()), Vector3.vector3_001));
    }

    public static void main(String[] args) {
        Sellmeier bk7 = new Sellmeier (1.03961212, 6.00069867e-3,
                0.231792344, 2.00179144e-2,
                1.01046945, 1.03560653e2);

        //**********************************************************************
        // Optical system definition

        /* anchor telescope */
        OpticalSystem.Builder systemBuilder = new OpticalSystem.Builder();

        // light source
        PointSource.Builder ps = new PointSource.Builder(PointSource.SourceInfinityMode.SourceAtInfinity, Vector3.vector3_001)
                .add_spectral_line(SpectralLine.d)
                .add_spectral_line(SpectralLine.C)
                .add_spectral_line(SpectralLine.F);
        systemBuilder.add(ps);

        // Newton telescope
        Newton.Builder newton = new Newton.Builder (
                Vector3Pair.position_000_001, // position
                1494.567 / 2.,   // focal len
                245.1,          // aperture diameter
                100,
        1.0);
        systemBuilder.add (newton);
        /* anchor corrector */

        // Wynne 4 lens corrector for parabolic mirrors
        Lens.Builder wynne
                = new Lens.Builder ().position(newton.get_focal_plane ())
                .zoffset(-48.4585); // z offset of first surface

        //  roc       ap.radius  thickness  material
        wynne.add_surface (21.496, 23.2 / 2., 1.905, bk7);
        wynne.add_surface (24.787, 22.5 / 2., 1.574);
        wynne.add_surface (55.890, 22.5 / 2., 1.270, bk7);
        wynne.add_surface (45.164, 21.8 / 2., 18.504);
        wynne.add_surface (29.410, 14.7 / 2., 0.45, bk7);
        wynne.add_surface (13.870, 14.1 / 2., 16.086);
        wynne.add_surface (23.617, 13.1 / 2., 1.805, bk7);
        wynne.add_surface (0, 12.8 / 2., 9.003);

        systemBuilder.add (wynne);

        // image plane
        Image.Builder image = new Image.Builder().position (get_exit_plane (wynne))
                .curve(Flat.flat)
                .shape(new Rectangle(.15 * 2.0));
        systemBuilder.add (image);



        /* anchor end */

        //**********************************************************************
        // Display some newton telescope parameters

        /* anchor print */
//        std::cout << "unvignetted image diameter: "
//                << newton->get_unvignetted_image_diameter () << std::endl;
//
//        std::cout << "secondary minor axis size: "
//                << newton->get_secondary_minor_axis () << std::endl;
//
//        std::cout << "secondary offset: " << newton->get_secondary_offset ()
//                << std::endl;
//
//        std::cout << "field angle: " << newton->get_field_angle () << std::endl;


        {
//            trace::Tracer tracer (sys.get ());
//
//            // set system entrance pupil (needed by non-sequential ray trace)
//            sys->set_entrance_pupil (newton->get_primary ());
//
//            // trace rays through the system
//            tracer.get_params ().set_default_distribution (
//                    trace::Distribution (trace::CrossDist, 5));
//            tracer.get_trace_result ().set_generated_save_state (*source);
//            tracer.trace ();

            /* anchor layout */
            RendererSvg svg_renderer = new RendererSvg ( 640, 480);
            RendererViewport renderer = svg_renderer;

            // horizontal page layout
            renderer.set_page_layout (1, 2);

            // 3d system layout on 1st sub-page
            renderer.set_page (0);
            renderer.set_perspective ();

            OpticalSystem sys = systemBuilder.build();
            SystemLayout3D layout = new SystemLayout3D();
            layout.layout3d(svg_renderer, sys);

//            tracer.get_trace_result ().draw_3d (renderer);

            // 2d Wynne corrector layout on 2nd sub-page
            renderer.set_page (1);

//            SystemLayout2D layout2D = new SystemLayout2D();
//            layout2D.layout2d(wynne);

//            tracer.get_trace_result ().draw_2d (renderer, false, wynne.get ());
            /* anchor end */

            System.out.println(svg_renderer.write(new StringBuilder()).toString());
        }
    }

}
