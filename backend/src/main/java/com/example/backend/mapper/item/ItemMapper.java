package com.example.backend.mapper.item;

import com.example.backend.dto.item.Item;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ItemMapper {
    @Insert("""
            INSERT INTO TB_ITEMMST
            (item_key, item_common_code, customer_code, input_price, output_price, size, unit, item_note)
            VALUES (#{itemKey}, #{itemCommonCode}, #{customerCode}, #{inputPrice}, #{outputPrice}, #{size}, #{unit}, #{itemNote})
            """)
    @Options(keyProperty = "itemKey", useGeneratedKeys = true)
    int addItem(Item item);

    @Select("""
            SELECT item_common_code, item_common_name
            FROM TB_ITEMCOMM
            ORDER BY binary(item_common_name)
            """)
    List<Map<String, String>> getItemCommonCode();

    @Select("""
            SELECT i.item_key, ic.item_common_name, c.customer_code, i.input_price, i.output_price, i.item_active
            FROM TB_ITEMMST i LEFT JOIN TB_ITEMCOMM ic ON i.item_common_code = ic.item_common_code
                              LEFT JOIN TB_CUSTMST c ON i.customer_code = c.customer_code
            """)
    List<Item> getItemList();

    @Select("""
            SELECT i.*, ic.item_common_name
            FROM TB_ITEMMST i LEFT JOIN TB_ITEMCOMM ic ON i.item_common_code = ic.item_common_code
            WHERE i.item_key = #{itemKey}
            """)
    List<Item> getItemView(Integer itemKey);
}
