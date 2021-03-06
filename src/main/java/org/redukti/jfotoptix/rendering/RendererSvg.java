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


package org.redukti.jfotoptix.rendering;

import org.redukti.jfotoptix.math.MathUtils;
import org.redukti.jfotoptix.math.Vector2;
import org.redukti.jfotoptix.math.Vector2Pair;

import java.text.DecimalFormat;
import java.util.EnumSet;

import static org.redukti.jfotoptix.rendering.Renderer.PointStyle.PointStyleCross;
import static org.redukti.jfotoptix.rendering.Renderer.Style.StyleBackground;

/**
 * SVG file rendering driver
 * <p>
 * This class implements a SVG graphic output driver.
 */
public class RendererSvg extends Renderer2d {

    StringBuilder _out = new StringBuilder();
    final DecimalFormat _decimal_format;

    String format(double value) {
        //return String.format("%.3f", value);
        return _decimal_format.format(value);
    }

    /**
     * Create a new svg renderer with given resolution. The
     *
     * write function must be used to write svg to output
     * stream.
     */
    public RendererSvg(double width, double height,
                Rgb bg) {
        super();

        _decimal_format = MathUtils.decimal_format();
        _2d_output_res = new Vector2(width, height);
        _styles_color[StyleBackground.value] = bg;
        _styles_color[Style.StyleForeground.value] = bg.negate();

        clear();
    }

    public RendererSvg(double width, double height) {
        this(width, height, Rgb.rgb_white);
    }

    /**
     * Create a new svg renderer with given resolution. The
     *
     * write function must be used to write svg to output
     * stream.
     */
    RendererSvg() {
        this(800, 600, Rgb.rgb_white);
    }

    void svg_begin_rect(double x1, double y1, double x2, double y2,
                        boolean terminate) {
        _out.append("<rect ")
                .append("x=\"").append(format(x1)).append("\" ")
                .append("y=\"").append(format(y1)).append("\" ")
                .append("width=\"").append(format(x2 - x1)).append("\" ")
                .append("height=\"").append(format(y2 - y1)).append("\" ");

        if (terminate)
            _out.append(" />").append(System.lineSeparator());
    }

    void svg_begin_line(double x1, double y1, double x2, double y2,
                        boolean terminate) {
        _out.append("<line ")
                .append("x1=\"").append(format(x1)).append("\" ")
                .append("y1=\"").append(format(y1)).append("\" ")
                .append("x2=\"").append(format(x2)).append("\" ")
                .append("y2=\"").append(format(y2)).append("\" ");

        if (terminate)
            _out.append(" />").append(System.lineSeparator());
    }

    void svg_begin_ellipse(double x, double y, double rx, double ry,
                           boolean terminate) {
        _out.append("<ellipse ")
                .append("cx=\"").append(format(x)).append("\" ")
                .append("cy=\"").append(format(y)).append("\" ")
                .append("rx=\"").append(format(rx)).append("\" ")
                .append("ry=\"").append(format(ry)).append("\" ");

        if (terminate)
            _out.append(" />").append(System.lineSeparator());
    }

    StringBuilder write_srgb(Rgb rgb) {
        _out.append(String.format("#%02x%02x%02x", (int) (rgb.r * 255.0),
                (int) (rgb.g * 255.0), (int) (rgb.b * 255.0)));
        return _out;
    }

    void svg_add_fill(Rgb rgb) {
        _out.append(" fill=\"");
        write_srgb(rgb);
        _out.append("\"");
    }

    void svg_end() {
        _out.append(" />").append(System.lineSeparator());
    }

    public void clear() {
        _out.setLength(0);

        // background
        svg_begin_rect(0.0, 0.0, _2d_output_res.x(), _2d_output_res.y(), false);
        svg_add_fill(get_style_color(StyleBackground));
        svg_end();

        _out.append("<defs>").append(System.lineSeparator());

        // dot shaped point
        _out.append("<g id=\"")
                .append("dot")
                .append("\">").append(System.lineSeparator());
        svg_begin_line(1, 1, 0, 0, true);
        _out.append("</g>").append(System.lineSeparator());

        // cross shaped point
        _out.append("<g id=\"")
                .append("cross")
                .append("\">").append(System.lineSeparator());
        svg_begin_line(-3, 0, 3, 0, true);
        svg_begin_line(0, -3, 0, 3, true);
        _out.append("</g>").append(System.lineSeparator());

        // square shaped point
        _out.append("<g id=\"")
                .append("square")
                .append("\">").append(System.lineSeparator());
        svg_begin_line(-3, -3, -3, 3, true);
        svg_begin_line(-3, 3, 3, 3, true);
        svg_begin_line(3, 3, 3, -3, true);
        svg_begin_line(3, -3, -3, -3, true);
        _out.append("</g>").append(System.lineSeparator());

        // round shaped point
        _out.append("<g id=\"")
                .append("round")
                .append("\">").append(System.lineSeparator());
        svg_begin_ellipse(0, 0, 3, 3, false);
        _out.append(" fill=\"none\" />");
        _out.append("</g>").append(System.lineSeparator());

        // triangle shaped point
        _out.append("<g id=\"")
                .append("triangle")
                .append("\">").append(System.lineSeparator());
        svg_begin_line(0, -3, -3, 3, true);
        svg_begin_line(-3, 3, 3, 3, true);
        svg_begin_line(0, -3, +3, +3, true);
        _out.append("</g>").append(System.lineSeparator());

        _out.append("</defs>").append(System.lineSeparator());
    }

    @Override
    public void group_begin(String name) {
        _out.append("<g>");
        if (!name.isEmpty())
            _out.append("<title>").append(name).append("</title>");
        _out.append(System.lineSeparator());
    }

    @Override
    public void group_end() {
        _out.append("</g>").append(System.lineSeparator());
    }

    void svg_begin_use(String id, double x, double y,
                       boolean terminate) {
        _out.append("<use ")
                .append("x=\"").append(format(x)).append("\" ")
                .append("y=\"").append(format(y)).append("\" ")
                .append("xlink:href=\"#").append(id).append("\" ");

        if (terminate)
            _out.append(" />").append(System.lineSeparator());
    }

    void svg_add_stroke(Rgb rgb) {
        _out.append(" stroke=\"");
        write_srgb(rgb);
        _out.append("\"");
    }

    void svg_add_id(String id) {
        _out.append(" fill=\"").append(id).append("\"");
    }

    static String[] ids = {"dot", "cross", "round", "square", "triangle"};

    @Override
    public void draw_point(Vector2 p, Rgb rgb, PointStyle s) {
        if (s.value >= ids.length)
            s = PointStyleCross;

        Vector2 v2d = trans_pos(p);

        svg_begin_use(ids[s.value], v2d.x(), v2d.y(), false);
        svg_add_stroke(rgb);
        svg_end();
    }

    @Override
    public void draw_segment(Vector2Pair l, Rgb rgb) {
        Vector2 v2da = trans_pos(l.v0);
        Vector2 v2db = trans_pos(l.v1);

        svg_begin_line(v2da.x(), v2da.y(), v2db.x(), v2db.y(), false);
        svg_add_stroke(rgb);
        svg_end();
    }

    @Override
    public void draw_circle(Vector2 c, double r, Rgb rgb, boolean filled) {
        Vector2 v2d = trans_pos(c);

        svg_begin_ellipse(v2d.x(), v2d.y(), x_scale(r), y_scale(r), false);
        svg_add_stroke(rgb);
        if (filled)
            svg_add_fill(rgb);
        else
            _out.append(" fill=\"none\"");
        svg_end();
    }

    @Override
    public void draw_text(Vector2 v, Vector2 dir,
                          String str, EnumSet<TextAlignMask> a, int size,
                          Rgb rgb) {
        int margin = size / 2;
        Vector2 v2d = trans_pos(v);
        double x = v2d.x();
        double y = v2d.y();
        double yo = y, xo = x;

        _out.append("<text style=\"font-size:").append(size).append(";");

        if (a.contains(TextAlignMask.TextAlignLeft)) {
            //_out << "text-align:left;text-anchor:start;";
            x += margin;
        } else if (a.contains(TextAlignMask.TextAlignRight)) {
            _out.append("text-align:right;text-anchor:end;");
            x -= margin;
        } else
            _out.append("text-align:center;text-anchor:middle;");

        if (a.contains(TextAlignMask.TextAlignTop))
            y += size + margin;
        else if (a.contains(TextAlignMask.TextAlignBottom))
            y -= margin;
        else
            y += size / 2.0;

        _out.append("\" x=\"").append(format(x)).append("\" y=\"").append(format(y)).append("\"");

        double ra = Math.toDegrees(Math.atan2(-dir.y(), dir.x()));
        if (ra != 0)
            _out.append(" transform=\"rotate(").append(format(ra)).append(",").append(format(xo)).append(",").append(format(yo)).append(")\"");

        svg_add_fill(rgb);

        _out.append(">").append(str).append("</text>").append(System.lineSeparator());
    }

    @Override
    public void draw_polygon(Vector2[] array,
                             Rgb rgb, boolean filled, boolean closed) {
        if (array.length < 3)
            return;

        closed = closed || filled;

        if (closed) {
            _out.append("<polygon");

            if (filled)
                svg_add_fill(rgb);
            else {
                _out.append(" fill=\"none\"");
                svg_add_stroke(rgb);
            }
        } else {
            _out.append("<polyline fill=\"none\"");

            svg_add_stroke(rgb);
        }

        _out.append(" points=\"");

        for (int i = 0; i < array.length; i++) {
            Vector2 v2d = trans_pos(array[i]);

            _out.append(format(v2d.x())).append(",").append(format(v2d.y())).append(" ");
        }

        _out.append("\" />").append(System.lineSeparator());
    }


    Vector2 trans_pos(Vector2 v) {
        return new Vector2(x_trans_pos(v.x()), y_trans_pos(v.y()));
    }

    double y_trans_pos(double y) {
        return (((y - _page.v1.y()) / (_page.v0.y() - _page.v1.y()))
                * _2d_output_res.y());
    }

    public StringBuilder write(StringBuilder s) {
        s.append("<?xml version=\"1.0\" standalone=\"no\"?>").append(System.lineSeparator());

        s.append("<svg width=\"").append(format(_2d_output_res.x())).append("px\" height=\"")
                .append(format(_2d_output_res.y())).append("px\" ")
                .append("version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" ")
                .append("xmlns:xlink=\"http://www.w3.org/1999/xlink\">")
                .append(System.lineSeparator());

        // content
        s.append(_out);

        s.append("</svg>").append(System.lineSeparator());
        return s;
    }

}
