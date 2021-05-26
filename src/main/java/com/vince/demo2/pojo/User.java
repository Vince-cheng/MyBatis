package com.vince.demo2.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

/**
 * @author vince
 * @since v1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
//@Alias("myUser")
@Alias("MYUSER")
public class User {
  private Long id;
  private String name;
  private Integer age;
  private Double salary;
  private Integer sex;
}
