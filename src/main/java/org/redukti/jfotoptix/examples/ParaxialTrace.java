package org.redukti.jfotoptix.examples;

import org.redukti.jfotoptix.math.Vector3Pair;
import org.redukti.jfotoptix.medium.GlassMap;
import org.redukti.jfotoptix.model.Lens;
import org.redukti.jfotoptix.model.OpticalSurface;
import org.redukti.jfotoptix.model.OpticalSystem;
import org.redukti.jfotoptix.parax.YNUTrace;
import org.redukti.jfotoptix.parax.YNUTraceData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParaxialTrace {

    public static void main(String[] args) throws Exception {

        /*
           Modern Optical Enginerring, W.J.Smith.
           Section 2.6, Example D.
         */
        OpticalSystem.Builder systemBuilder = new OpticalSystem.Builder();
        Lens.Builder lensBuilder = new Lens.Builder()
                .position(Vector3Pair.position_000_001)
                .add_surface(50.0,  10.0, 10,
                        new GlassMap("Glass1.5", 1.5, 1.5, 1.5))
                .add_surface(-50.0, 10.0, 2.0,
                        new GlassMap("Glass1.6", 1.6, 1.6, 1.6))
                .add_surface(0.0,  10.0, 30.0);
        systemBuilder.add(lensBuilder);
        OpticalSystem system = systemBuilder.build();

        YNUTrace ynuTrace = new YNUTrace();
        List<OpticalSurface> seq = system.get_sequence().stream().filter(e -> e instanceof OpticalSurface).map(e -> (OpticalSurface)e).collect(Collectors.toList());
        Map<Integer, YNUTraceData> data = ynuTrace.trace(seq, 10.0, 0.0333, 0);
        System.out.println(-data.get(seq.get(seq.size()-1).id()).height/data.get(seq.get(seq.size()-1).id()).slope);
    }
}
