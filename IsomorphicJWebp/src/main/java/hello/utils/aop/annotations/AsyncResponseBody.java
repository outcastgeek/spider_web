package hello.utils.aop.annotations;

import java.lang.annotation.*;

/**
 * Created by outcastgeek on 4/12/15.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncResponseBody {
}
