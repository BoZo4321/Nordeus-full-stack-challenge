package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.RunState;

import java.util.List;

public record StartRunResponse(
        String runId,
        HeroSummaryView hero,
        List<EncounterView> encounters
) {
    public static StartRunResponse from(RunState run) {
        List<EncounterView> encounterViews = run.getEncounters().stream()
                .map(EncounterView::from)
                .toList();
        return new StartRunResponse(
                run.getRunId(),
                HeroSummaryView.from(run),
                encounterViews
        );
    }
}
