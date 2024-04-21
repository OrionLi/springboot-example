package io.github.orionli.springbootexample.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @TableName user
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class User implements Serializable {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 性别
     */
    private String sex;

    /**
     * 注册时间
     */
    private LocalDateTime registerDate;

    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifyDate;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 4690279493819688482L;

    //初始化分布式自增id生成器，详情看README
    static {
        YitIdHelper.setIdGenerator(new IdGeneratorOptions());
    }

    public static long generateId() {
        return YitIdHelper.nextId();
    }

}