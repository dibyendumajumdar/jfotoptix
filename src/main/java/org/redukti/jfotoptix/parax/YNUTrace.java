package org.redukti.jfotoptix.parax;

import org.redukti.jfotoptix.light.SpectralLine;
import org.redukti.jfotoptix.medium.Medium;
import org.redukti.jfotoptix.model.*;

import java.util.List;

public class YNUTrace {

    public void trace(OpticalSystem system, double l1, double initial_height) {

        List<Element> seq = system.get_sequence();
        System.out.println(seq);

        /*
           The implementation below is based on description in
           Modern Optical Enginerring, W.J.Smith.
           Section 2.6, Example D.
         */

        double y1 = initial_height;
        double u1 = -y1/l1;
        double y2 = y1;
        double stopThickness = 0;
        for (Element e: seq) {
            if (e instanceof OpticalSurface) {
                y1 = y2;
                OpticalSurface surface = (OpticalSurface) e;
                Medium leftMedium = surface.get_material(0);
                double t1 = surface.get_thickness()+stopThickness;
                Medium rightMedium = surface.get_material(1);
                double C1 = surface.get_curve().get_curvature();
                double n1 = leftMedium.get_refractive_index(SpectralLine.d);
                double n1_ = rightMedium.get_refractive_index(SpectralLine.d);
                double n1_u1_ = -y1 *(n1_ - n1) * C1 + n1*u1;
                y2 = y1 + t1 * (n1_u1_)/n1_;
                u1 = n1_u1_/n1_;
                stopThickness = 0.0; // Not a stop
            }
            else if (e instanceof Stop) {
                stopThickness = ((Stop)e).get_thickness();
                continue;
            }
            else {
                continue;
            }
        }
        double l = -y1/u1;
        System.out.println(l);
    }

}
