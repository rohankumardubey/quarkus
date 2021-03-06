package io.quarkus.deployment.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.quarkus.builder.item.BuildItem;

/**
 * Indicates that this {@link BuildStep} method will also output recorded bytecode.
 *
 * If this annotation is present at least one method parameter must be a recorder object
 * (i.e. a runtime object annotated with {@code @Recorder}). Any invocations made against
 * this object will be recorded, and written out to bytecode to be invoked at runtime.
 *
 * The {@link #value()} element determines when the generated bytecode is executed. If this
 * is {@link ExecutionTime#STATIC_INIT} then it will be executed from a static init method,
 * so will run at native image generation time.
 *
 * If this is {@link ExecutionTime#RUNTIME_INIT} then it will run from a main method at application
 * start.
 *
 * There are some limitations on what can be recorded. Only the following objects are allowed as parameters to
 * recording proxies:
 * <p>
 * - primitives
 * - String
 * - Class
 * - Objects returned from a previous recorder invocation
 * - Objects with a no-arg constructor and getter/setters for all properties (or public fields)
 * - Objects with a constructor annotated with @RecordableConstructor with parameter names that match field names
 * - Any arbitrary object via the
 * {@link io.quarkus.deployment.recording.RecorderContext#registerSubstitution(Class, Class, Class)} mechanism
 * - arrays, lists and maps of the above
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Record {

    /**
     * The time to execute the recorded bytecode
     */
    ExecutionTime value();

    /**
     * If this is true then the bytecode produced by this method will be considered to be optional,
     * and will only be created if this build step also produces another {@link BuildItem}
     * that is consumed by the build.
     *
     * If a method is optional it must be capable of producing at least one other item
     *
     */
    boolean optional() default false;

    /**
     * If this is set to false then parameters are considered equal based on equals/hashCode, instead of identity.
     *
     * This is an advanced option, it is only useful if you are recording lots of objects that you expect to be the same
     * but have different identities. This allows multiple objects at deployment time to be interned into a single
     * object at runtime.
     *
     * This is an advanced option, most recorders don't want this.
     */
    boolean useIdentityComparisonForParameters() default true;

}
