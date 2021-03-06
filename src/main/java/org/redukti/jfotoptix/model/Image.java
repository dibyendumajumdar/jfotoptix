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


package org.redukti.jfotoptix.model;

import org.redukti.jfotoptix.curve.Curve;
import org.redukti.jfotoptix.math.Transform3;
import org.redukti.jfotoptix.math.Vector3Pair;
import org.redukti.jfotoptix.shape.Shape;

public class Image extends Surface {

    public Image(int id, Vector3Pair p, Transform3 transform, Curve curve, Shape shape) {
        super(id, p, transform, curve, shape, 0.0);
    }

    @Override
    public String toString() {
        return "Image{" + super.toString() + "}";
    }

    public static class Builder extends Surface.Builder {
        @Override
        public Image.Builder position(Vector3Pair position) {
            return (Image.Builder) super.position(position);
        }

        public Image.Builder shape(Shape shape) {
            return (Image.Builder) super.shape(shape);
        }

        public Image.Builder curve(Curve curve) {
            return (Image.Builder) super.curve(curve);
        }

        public Image build() {
            return new Image(_id, _position, _transform, curve, shape);
        }
    }
}
