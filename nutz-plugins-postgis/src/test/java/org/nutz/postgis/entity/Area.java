package org.nutz.postgis.entity;

import java.math.BigDecimal;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.postgis.dao.JtsGeometryValueAdapter;
import org.postgis.jts.JtsGeometry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 地区
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Comment("地区")
@Table(value = "t_area")
@EqualsAndHashCode(callSuper = false, of = "code")
public class Area {

    @Name
    @Column("a_ad_code")
    private String code;

    @Column(value = "a_center")
    @ColDefine(type = ColType.BINARY, customType = "geometry", adaptor = JtsGeometryValueAdapter.class)
    private JtsGeometry center;

    @Column(value = "a_boundary")
    @ColDefine(type = ColType.BINARY, customType = "geometry", adaptor = JtsGeometryValueAdapter.class)
    private JtsGeometry boundary;

    @Column("a_longitude")
    private BigDecimal longitude;

    @Column("a_latitude")
    private BigDecimal latitude;

    @Column("a_name")
    private String name;

    @Column("a_level")
    private String level;

    @Column("a_parent")
    private String parent;

    @Column("a_tele_code")
    private String teleCode;

}
