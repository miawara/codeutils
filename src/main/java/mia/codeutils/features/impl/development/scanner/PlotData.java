package mia.codeutils.features.impl.development.scanner;

import java.io.File;

public record PlotData(
        String owner,
        String plotName,
        int plotId,
        String node
) { }