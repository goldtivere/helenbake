package com.helenbake.helenbake.repo.predicate;



import com.helenbake.helenbake.domain.enums.RoleType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Created by Toyin on 2/17/19.
 */
public class EnumBooleanExpression {
    public static  <T> BooleanExpression getExpression(String key, Object value, PathBuilder<T> entityPath) {
        switch (key) {
            case "role.name":
                return entityPath.getEnum(key, RoleType.class).eq((RoleType) value);
        }
        return null;
    }
}
