package org.gecko.vaadin.generator;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.vaadin.flow.server.frontend.scanner.ClassFinder;

public class GeneratorClassFinder implements ClassFinder {
	
	private static final long serialVersionUID = -2956251217598387337L;
	private final Reflections reflections;
	private final ClassLoader classLoader;

	public GeneratorClassFinder(ConfigurationBuilder configBuilder, ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.reflections = new Reflections(configBuilder);
	}

    @Override
    public Set<Class<?>> getAnnotatedClasses(
            Class<? extends Annotation> clazz) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.addAll(reflections.getTypesAnnotatedWith(clazz, true));
        classes.addAll(getAnnotatedByRepeatedAnnotation(clazz));
        return sortedByClassName(classes);

    }

    private Set<Class<?>> getAnnotatedByRepeatedAnnotation(
            AnnotatedElement annotationClass) {
        Repeatable repeatableAnnotation = annotationClass
                .getAnnotation(Repeatable.class);
        if (repeatableAnnotation != null) {
            return reflections
                    .getTypesAnnotatedWith(repeatableAnnotation.value(), true);
        }
        return Collections.emptySet();
    }

    @Override
    public URL getResource(String name) {
        return classLoader.getResource(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> loadClass(String name) throws ClassNotFoundException {
        return (Class<T>) classLoader.loadClass(name);
    }

    @Override
    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
        return sortedByClassName(reflections.getSubTypesOf(type));
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    private <T> Set<Class<? extends T>> sortedByClassName(
            Set<Class<? extends T>> source) {
        return source.stream().sorted(Comparator.comparing(Class::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
