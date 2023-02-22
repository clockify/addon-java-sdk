package annotationprocessor;

import annotationprocessor.clockify.ClockifyManifestProcessor;
import annotationprocessor.clockify.ExtendClockifyManifest;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_18)
@NoArgsConstructor
public class ManifestExtensionProcessor extends javax.annotation.processing.AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(ExtendClockifyManifest.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            try {
                processAnnotation(annotation, roundEnv);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @SneakyThrows
    private void processAnnotation(TypeElement annotation, RoundEnvironment roundEnv) {
        String annotationName = annotation.getQualifiedName().toString();

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        List<JavaFile> files = new LinkedList<>();

        for (Element element : elements) {
            ExtendClockifyManifest ann = element.getAnnotation(ExtendClockifyManifest.class);

            DeclaredType type = (DeclaredType) element.asType();
            Processor processor = new ClockifyManifestProcessor(type, ann.definition());

            if (isSupported(processor, annotationName)) {
                files.addAll(processor.process());
            }
        }

        for (JavaFile file : files) {
            file.writeTo(processingEnv.getFiler());
        }
    }

    private boolean isSupported(Processor processor, String annotationName) {
        return processor.getSupportedAnnotation().getCanonicalName().equals(annotationName);
    }
}
