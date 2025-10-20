package cloud.neko_lkc1009.nekoshare.common.base;

import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Objects;

public interface BaseConvert<S, T> extends Converter<S, T> {
    @Override
    T convert(S source);

    @Override
    default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
        return Converter.super.andThen(after);
    }

    S reverse(T target);

    default List<T> convertList(List<S> sourceList) {
        return Objects.isNull(sourceList) ? null : sourceList.stream().map(this::convert).toList();
    }

    default List<S> reverseList(List<T> targetList) {
        return Objects.isNull(targetList) ? null : targetList.stream().map(this::reverse).toList();
    }

}
