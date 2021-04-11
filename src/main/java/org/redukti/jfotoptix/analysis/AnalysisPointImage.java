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

package org.redukti.jfotoptix.analysis;

import org.redukti.jfotoptix.patterns.Distribution;
import org.redukti.jfotoptix.patterns.Pattern;
import org.redukti.jfotoptix.sys.Image;
import org.redukti.jfotoptix.sys.OpticalSystem;
import org.redukti.jfotoptix.tracing.RayTraceParameters;
import org.redukti.jfotoptix.tracing.RayTraceResults;
import org.redukti.jfotoptix.tracing.RayTracer;
import org.redukti.jfotoptix.tracing.TracedRay;

import java.util.ArrayList;
import java.util.List;

/**
 * Point image analysis base class
 */
public class AnalysisPointImage {
    OpticalSystem _system;
    RayTracer _tracer;
    RayTraceParameters _params;
    boolean _processed_trace;
    /** Image plan that collect rays for analysis */
    Image _image;
    /** The rays intercepted at image place */
    List<TracedRay> _intercepts;
    RayTraceResults _results;

    public AnalysisPointImage (OpticalSystem system)
    {
        _system = system;
        _tracer = new RayTracer();
        _processed_trace = false;
        _image = null;
        _intercepts = new ArrayList<>();
        _params = new RayTraceParameters(system);
        _params.set_default_distribution (
                new Distribution (Pattern.HexaPolarDist, 20, 0.999));
        _params.get_default_distribution ().set_uniform_pattern ();
    }

    public void trace() {
        if (_processed_trace)
            return;

        _image = (Image) _params.get_sequence().stream().filter(e-> e instanceof Image).findFirst().get();
        _results = _tracer.trace(_system, _params);
        _intercepts = _results.get_intercepted(_image);

        _processed_trace = true;
    }
}