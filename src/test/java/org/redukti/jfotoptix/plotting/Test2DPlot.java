package org.redukti.jfotoptix.plotting;

import org.junit.jupiter.api.Test;
import org.redukti.jfotoptix.data.DiscreteSet;
import org.redukti.jfotoptix.rendering.RendererSvg;
import org.redukti.jfotoptix.rendering.Rgb;

import static org.redukti.jfotoptix.data.Interpolation.Cubic2;

public class Test2DPlot {


    @Test
    public void test_2d_plot() {
        DiscreteSet d1 = new DiscreteSet();

        final double N = 40.0;
        double x = -N / 2.0;

        for (int i = 0; i < N; i++) {
            d1.add_data(x, Math.cos(x / 3.) * Math.cos(x) / 2., 0.0);
            x += Math.abs(Math.sin(i) + .5);
        }
        d1.set_interpolation(Cubic2);

        Plot p = new Plot();

        p.get_axes().set_label("The X axis", PlotAxes.AxisMask.X);
        p.get_axes().set_label("The Y axis", PlotAxes.AxisMask.Y);
        p.set_title("A simple test plot");

        p.add_plot_data(d1, Rgb.rgb_red, "data",  PlotStyleMask.InterpolatePlot.value() | PlotStyleMask.PointPlot.value());

        PlotRenderer plot_r = new PlotRenderer();
        RendererSvg renderer = new RendererSvg(800, 600);
        plot_r.draw_plot(renderer, p);
        System.out.println(renderer.write(new StringBuilder()).toString());
    }
}
