package io.quarkiverse.githubaction.deployment;

import java.util.Set;

import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.DotName;

import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.processor.DotNames;

class VetoUserDefinedEventListeningClassesAnnotationsTransformer implements AnnotationsTransformer {

    private final Set<DotName> eventDefinitionAnnotations;

    VetoUserDefinedEventListeningClassesAnnotationsTransformer(Set<DotName> eventDefinitionAnnotations) {
        this.eventDefinitionAnnotations = eventDefinitionAnnotations;
    }

    @Override
    public boolean appliesTo(Kind kind) {
        return Kind.CLASS.equals(kind);
    }

    @Override
    public void transform(TransformationContext transformationContext) {
        Set<DotName> annotations = transformationContext.getTarget().asClass().annotations().keySet();

        if (annotations.contains(GitHubActionDotNames.MULTIPLEXER)) {
            return;
        }

        if (annotations.contains(GitHubActionDotNames.ACTION)) {
            transformationContext.transform().add(DotNames.VETOED).done();
            return;
        }

        for (DotName eventDefiningAnnotation : eventDefinitionAnnotations) {
            if (!annotations.contains(eventDefiningAnnotation)) {
                continue;
            }

            transformationContext.transform().add(DotNames.VETOED).done();
            return;
        }
    }

}
