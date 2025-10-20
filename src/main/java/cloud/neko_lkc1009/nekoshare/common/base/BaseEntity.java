package cloud.neko_lkc1009.nekoshare.common.base;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class BaseEntity<T> {
    protected Long id;

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    protected T setId(Long id) {
        this.id = id;
        return self();
    }

}
