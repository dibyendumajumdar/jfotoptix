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

import java.text.DecimalFormat;

public class MathUtils {
    public static double square(double x) { return x*x; }
    public static int trunc(double value) {
        return (int)(value<0 ? Math.ceil(value) : Math.floor(value));
    }
    public static DecimalFormat decimal_format(int maxFractionDigits) {
        DecimalFormat _decimal_format = new DecimalFormat();
        //formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        _decimal_format.setMinimumIntegerDigits(1);
        _decimal_format.setMaximumFractionDigits(maxFractionDigits);
        _decimal_format.setMinimumFractionDigits(0);
        _decimal_format.setDecimalSeparatorAlwaysShown(false);
        _decimal_format.setGroupingUsed(false);
        return _decimal_format;
    }
    public static DecimalFormat decimal_format() {
        return decimal_format(3);
    }
    public static DecimalFormat decimal_format_scientific(int maxFractionDigits) {
        DecimalFormat _decimal_format = new DecimalFormat("0.0E0");
        //formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        _decimal_format.setMinimumIntegerDigits(1);
        _decimal_format.setMaximumFractionDigits(maxFractionDigits);
        _decimal_format.setDecimalSeparatorAlwaysShown(false);
        _decimal_format.setGroupingUsed(true);
        return _decimal_format;
    }

}
