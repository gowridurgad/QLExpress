package com.alibaba.qlexpress4.runtime.data.convert;


import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:40
 */
public class BooleanConversion {
    public static QLConvertResult trans(Object object) {
        //delete null to boolean
        if (object.getClass() == boolean.class || object.getClass() == Boolean.class) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS,(Boolean) object);
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
    }

}