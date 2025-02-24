package com.example.backend.mapper.state.retrieve;

import com.example.backend.dto.state.retrieve.Return;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReturnMapper {

    //반품 관리 리스트
    @Select("""
            <script>  
            SELECT rr.return_request_key, rr.franchise_code, f.franchise_name, ra.return_no, rr.serial_no, itc.common_code_name itemCommonName, 
                   rr.business_employee_no, emb.employee_name AS businessEmployeeName, rr.customer_code, customer_name, 
                   customer_employee_no, emce.employee_name customerEmployeeName, customer_configurer_no, emcc.employee_name customerConfigurerName,
                   return_request_date, return_approve_date, disapprove_date, return_consent, disapprove_employee_no, emd.employee_name disapproveEmployeeName
            FROM TB_RTN_REQ rr
            LEFT JOIN TB_RTN_APPR ra
            ON ra.return_request_key = rr.return_request_key
            LEFT JOIN TB_FRNCHSMST f ON f.franchise_code = rr.franchise_code
            LEFT JOIN TB_CUSTMST c ON c.customer_code = rr.customer_code
            LEFT JOIN TB_ITEMSUB its ON its.serial_no = rr.serial_no
            LEFT JOIN TB_SYSCOMM itc ON itc.common_code = its.item_common_code
            LEFT JOIN TB_EMPMST emb ON emb.employee_no = rr.business_employee_no
            LEFT JOIN TB_EMPMST emce ON emce.employee_no = ra.customer_employee_no
            LEFT JOIN TB_EMPMST emcc ON emcc.employee_no = ra.customer_configurer_no
            LEFT JOIN (SELECT * FROM TB_DISPR WHERE state_common_code='RETRN') d ON rr.return_request_key = d.state_request_key
            LEFT JOIN TB_EMPMST emd ON emd.employee_no = d.disapprove_employee_no
            WHERE
                <if test="state == 'all'">
                    (1=1 || return_consent IS NOT true || return_consent IS NOT false)
                </if>
                <if test="state == 'request'">
                    return_consent IS NULL
                </if>
                <if test="state == 'approve'">
                    return_consent = true
                </if>
                <if test="state == 'disapprove'">
                    return_consent = false
                </if>
            
                <!-- userCompany 값에 따라 조건 추가 -->
                <if test="userCompany != null">
                     <choose>
                        <when test="userCompany.startsWith('CUS')">
                            AND rr.customer_code = #{userCompany}
                        </when>
                        <when test="userCompany.startsWith('BIZ')">
                            AND 1=1
                        </when>
                     </choose>
                </if>
            
                <if test="keyword != null and keyword.trim()!=''">
                    AND (
                        <trim prefixOverrides="OR">
                            <if test="type=='all' or type=='franchiseName'">
                                franchise_name LIKE CONCAT('%', #{keyword}, '%')
                            </if>                
                            <if test="type=='all' or type=='serialNo'">
                                OR rr.serial_no LIKE CONCAT('%', #{keyword}, '%')
                            </if>
                            <if test="type=='all' or type=='itemCommonName'">
                                OR itc.common_code_name LIKE CONCAT('%', #{keyword}, '%')
                            </if>
                            <if test="type=='all' or type=='returnNo'">
                                OR ra.return_no LIKE CONCAT('%', #{keyword}, '%')
                            </if>                
                            <if test="type=='all' or type=='customerName'">
                                OR customer_name LIKE CONCAT('%', #{keyword}, '%')
                            </if>
                            <if test="type=='all' or type=='businessEmployeeName'">
                                OR rr.business_employee_no LIKE CONCAT('%', #{keyword}, '%')
                                OR emb.employee_name LIKE CONCAT('%', #{keyword}, '%')
                            </if>
                            <if test="type=='all' or type=='customerEmployeeName'">
                                OR ra.customer_employee_no LIKE CONCAT('%', #{keyword}, '%')
                                OR emce.employee_name LIKE CONCAT('%', #{keyword}, '%')                                                    
                                OR disapprove_employee_no LIKE CONCAT('%', #{keyword}, '%')
                                OR emd.employee_name LIKE CONCAT('%', #{keyword}, '%')
                            </if>
                            <if test="type=='all' or type=='customerConfigurerName'">
                                OR ra.customer_configurer_no LIKE CONCAT('%', #{keyword}, '%')
                                OR emcc.employee_name LIKE CONCAT('%', #{keyword}, '%')
                            </if>             
                        </trim>
                    )    
                </if>
            ORDER BY ${sort} ${order}
            LIMIT #{offset}, 10
            </script>
            """)
    List<Return> getReturnList(String state, Integer offset, String type, String keyword, String sort, String order, String userCompany);

    //입출 내역 생기면 franchise도 불러올 수 있음
    @Select("""
            SELECT common_code_name itemCommonName, its.item_common_code, customer_name, customer_code
            FROM TB_ITEMSUB its
            LEFT JOIN TB_SYSCOMM itc ON itc.common_code = its.item_common_code
            LEFT JOIN TB_CUSTMST c ON c.item_code = its.item_common_code
            WHERE serial_no=#{serialNo}
            """)
    List<Return> getStandardInfo(String serialNo);

    //요청 저장
    @Insert("""
            INSERT INTO TB_RTN_REQ
            (serial_no, franchise_code, business_employee_no, customer_code, return_consent, return_request_note) 
            VALUES (#{serialNo}, #{franchiseCode}, #{businessEmployeeNo}, #{customerCode}, #{returnConsent}, #{returnRequestNote})
            """)
    int addRequest(Return requestInfo);

    //요청/승인 내용 (1개) 반환
    @Select("""
            SELECT rr.return_request_key, rr.franchise_code, f.franchise_name, ra.return_no, rr.serial_no, itc.common_code_name itemCommonName, 
                   rr.business_employee_no, emb.employee_name AS businessEmployeeName, customer_employee_no, emce.employee_name AS customerEmployeeName, 
                   customer_configurer_no, emcc.employee_name AS customerConfigurerName, rr.customer_code, customer_name, ra.customer_employee_no,
                   return_request_date, return_approve_date,return_date, return_consent, return_request_note, return_approve_note,
                   disapprove_employee_no, disapprove_date, disapprove_note, emd.employee_name disapproveEmployeeName
            FROM TB_RTN_REQ rr
            LEFT JOIN TB_RTN_APPR ra
            ON ra.return_request_key = rr.return_request_key
            LEFT JOIN TB_FRNCHSMST f ON f.franchise_code = rr.franchise_code
            LEFT JOIN TB_CUSTMST c ON c.customer_code = rr.customer_code
            LEFT JOIN TB_ITEMSUB its ON its.serial_no = rr.serial_no
            LEFT JOIN TB_SYSCOMM itc ON itc.common_code = its.item_common_code
            LEFT JOIN TB_EMPMST emb ON emb.employee_no = rr.business_employee_no
            LEFT JOIN TB_EMPMST emce ON emce.employee_no = ra.customer_employee_no    
            LEFT JOIN TB_EMPMST emcc ON emcc.employee_no = ra.customer_configurer_no
            LEFT JOIN (SELECT * FROM TB_DISPR WHERE state_common_code='RETRN') d ON rr.return_request_key = d.state_request_key
            LEFT JOIN TB_EMPMST emd ON emd.employee_no = d.disapprove_employee_no    
            WHERE rr.return_request_key = #{returnRequestKey};
            """)
    List<Return> getRequestInfo(String returnRequestKey);

    //return consent 승인으로 변경
    @Update("""
            UPDATE TB_RTN_REQ
            SET return_consent=true
            WHERE return_request_key=#{returnRequestKey}
            """)
    int changeConsent(Integer returnRequestKey);

    //승인 정보 테이블에 추가
    @Insert("""
            INSERT INTO TB_RTN_APPR
            (return_request_key, customer_configurer_no, customer_employee_no, return_no, return_date, return_approve_note) 
            VALUES (#{returnRequestKey}, #{customerConfigurerNo}, #{customerEmployeeNo}, #{returnNo}, #{returnDate}, #{returnApproveNote})
            """)
    int addApprove(Return approveInfo);

    //발주 최대값 조회
    @Select("""
            <script>
                SELECT COALESCE(MAX(CAST(SUBSTRING(return_no, 4) AS UNSIGNED)), 0) AS maxReturnNo
                FROM TB_RTN_APPR
            </script>
            """)
    Integer viewMaxReturnNo();

    @Select("""
            <script>  
            SELECT COUNT(*)
            FROM TB_RTN_REQ rr
            LEFT JOIN TB_RTN_APPR ra
            ON ra.return_request_key = rr.return_request_key
            LEFT JOIN TB_FRNCHSMST f ON f.franchise_code = rr.franchise_code
            LEFT JOIN TB_CUSTMST c ON c.customer_code = rr.customer_code
            LEFT JOIN TB_ITEMSUB its ON its.serial_no = rr.serial_no
            LEFT JOIN TB_SYSCOMM itc ON itc.common_code = its.item_common_code
            LEFT JOIN TB_EMPMST emb ON emb.employee_no = rr.business_employee_no
            LEFT JOIN TB_EMPMST emce ON emce.employee_no = ra.customer_employee_no
            LEFT JOIN TB_EMPMST emcc ON emcc.employee_no = ra.customer_configurer_no
            LEFT JOIN (SELECT * FROM TB_DISPR WHERE state_common_code='RETRN') d ON rr.return_request_key = d.state_request_key
            LEFT JOIN TB_EMPMST emd ON emd.employee_no = d.disapprove_employee_no
            WHERE  
            <if test="state == 'all'">
                (1=1 || return_consent IS NOT true || return_consent IS NOT false)
            </if>
            <if test="state == 'request'">
               return_consent IS NULL
            </if>
            <if test="state == 'approve'">
                return_consent = true
            </if>
            <if test="state == 'disapprove'">
                return_consent = false
            </if>
            
            <!-- userCompany 값에 따라 조건 추가 -->
            <if test="userCompany != null">
                <choose>
                    <when test="userCompany.startsWith('CUS')">
                        AND rr.customer_code = #{userCompany}
                    </when>
                    <when test="userCompany.startsWith('BIZ')">
                        AND 1=1
                    </when>
                </choose>
            </if>
            
            <if test="keyword != null and keyword.trim()!=''">
                AND (
                    <trim prefixOverrides="OR">
                        <if test="type=='all' or type=='franchiseName'">
                            franchise_name LIKE CONCAT('%', #{keyword}, '%')
                        </if>                
                        <if test="type=='all' or type=='serialNo'">
                            OR rr.serial_no LIKE CONCAT('%', #{keyword}, '%')
                        </if>
                        <if test="type=='all' or type=='itemCommonName'">
                            OR itc.common_code_name LIKE CONCAT('%', #{keyword}, '%')
                        </if>
                        <if test="type=='all' or type=='returnNo'">
                            OR ra.return_no LIKE CONCAT('%', #{keyword}, '%')
                        </if>                
                        <if test="type=='all' or type=='customerName'">
                            OR customer_name LIKE CONCAT('%', #{keyword}, '%')
                        </if>
                        <if test="type=='all' or type=='businessEmployeeName'">
                            OR rr.business_employee_no LIKE CONCAT('%', #{keyword}, '%')
                            OR emb.employee_name LIKE CONCAT('%', #{keyword}, '%')
                        </if>
                        <if test="type=='all' or type=='customerEmployeeName'">
                            OR ra.customer_employee_no LIKE CONCAT('%', #{keyword}, '%')
                            OR emce.employee_name LIKE CONCAT('%', #{keyword}, '%')
                            OR disapprove_employee_no LIKE CONCAT('%', #{keyword}, '%')
                            OR emd.employee_name LIKE CONCAT('%', #{keyword}, '%')
                        </if>
                        <if test="type=='all' or type=='customerConfigurerName'">
                            OR ra.customer_configurer_no LIKE CONCAT('%', #{keyword}, '%')
                            OR emcc.employee_name LIKE CONCAT('%', #{keyword}, '%')
                        </if>    
                    </trim>
                )    
            </if>
            </script>      
            """)
    int countAll(String state, String type, String keyword, String userCompany);

    //반려: consent = false
    @Update("""
            UPDATE TB_RTN_REQ
            SET return_consent = false
            WHERE return_request_key=#{returnRequestKey}
            """)
    int disapproveReturn(Integer returnRequestKey);

    //반품 반려 : 반려테이블이 삽입
    @Insert("""
            INSERT INTO TB_DISPR
            (state_request_key, state_common_code, disapprove_employee_no, disapprove_note) 
            VALUES (#{returnRequestKey}, 'RETRN', #{customerEmployeeNo}, #{returnApproveNote})
            """)
    int addDisapprove(Return disapproveInfo);


    // 가맹점 코드로 시리얼 번호 목록 반환 (대기, 승인인 것 제외)
    @Select("""
            SELECT ioh.serial_no, common_code_name itemCommonName
            FROM TB_ITEMSUB its
                     LEFT JOIN TB_INOUT_HIS ioh
                               ON ioh.serial_no = its.serial_no
                     LEFT JOIN TB_SYSCOMM sc
                               ON its.item_common_code = sc.common_code
            WHERE current_common_code = 'FRN'
              AND franchise_code = #{franchiseCode}
            EXCEPT
            (SELECT rr.serial_no, common_code_name itemCommonName
             FROM TB_RTN_REQ rr
                LEFT JOIN TB_ITEMSUB its
                    ON rr.serial_no = its.serial_no
                LEFT JOIN TB_SYSCOMM sc
                    ON its.item_common_code = sc.common_code
             WHERE return_consent IS NULL || rr.return_consent = true
                 AND franchise_code = #{franchiseCode})
            """)
    List<Return> getSerialNoList(String franchiseCode);

    //협력 업체의 직원(기사 조회)
    @Select("""
            SELECT e.employee_name AS customerConfigurerName, e.employee_no AS customerConfigurerNo
            FROM TB_RTN_REQ r
            LEFT JOIN TB_EMPMST e ON r.customer_code =  e.employee_workplace_code
            WHERE r.return_request_key = #{returnRequestKey}
              AND e.employee_active = true;
            """)
    List<Map<String, Object>> getConfigurerList(String returnRequestKey);

}
