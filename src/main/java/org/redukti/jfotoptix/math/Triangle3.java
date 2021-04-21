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


package org.redukti.jfotoptix.math;

public class Triangle3 {
    final int N = 3;
    public final Vector3[] _v;

    public Triangle3(Vector3 a, Vector3 b, Vector3 c)
    {
        _v = new Vector3[N];
        _v[0] = a;
        _v[1] = b;
        _v[2] = c;
    }

    public Vector3 normal ()
    {
        return _v[0].minus(_v[1]).cross(_v[0].minus(_v[2])).normalize();
    }

    public Vector3 v(int i) {
        return _v[i];
    }

    public Triangle3 v(int i, Vector3 v) {
        switch(i) {
            case 0: return new Triangle3(v, _v[1], _v[2]);
            case 1: return new Triangle3(_v[0], v, _v[2]);
            case 2: return new Triangle3(_v[0], _v[1], v);
            default: throw new IllegalArgumentException();
        }
    }

    public Vector3[] asArray() {
        return _v.clone();
    }
}
