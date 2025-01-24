CREATE TABLE TB_INSTL_CONF
(
    install_configuration_key INT PRIMARY KEY AUTO_INCREMENT,
    output_no                 VARCHAR(13) NOT NULL,
    install_configuration     BOOLEAN
);

SELECT DISTINCT ir.install_request_key            as installRequestKey,
                ia.install_approve_key,
                f.franchise_name                  as franchiseName,
                sc.common_code_name               as itemCommonName,
                c.customer_name                   as customerName,
                ir.business_employee_no,
                e1.employee_name                  as businessEmployeeName,
                w.warehouse_name                  as warehouseName,
                ir.install_request_consent        as requestConsent,
                ia.output_no                      as outputNo,
                ia.customer_employee_no,
                e2.employee_name                  as customerEmployeeName,
                e3.employee_name                  as customerInstallerName,
                ia.install_approve_date           as installApproveDate,
                ir.install_request_date           as installRequestDate,
                ih.inout_history_date             as inoutHistoryDate,
                ia.install_approve_consent        as approveConsent,
                COALESCE(GREATEST(ir.install_request_date, ia.install_approve_date),
                         ir.install_request_date,
                         ia.install_approve_date) AS installDate
FROM TB_INSTL_REQ ir
         LEFT JOIN TB_INSTL_APPR ia ON ir.install_request_key = ia.install_request_key
         LEFT JOIN TB_FRNCHSMST f ON ir.franchise_code = f.franchise_code
         LEFT JOIN TB_SYSCOMM sc ON ir.item_common_code = sc.common_code
         LEFT JOIN TB_EMPMST e1 ON ir.business_employee_no = e1.employee_no -- 신청자 조인
         LEFT JOIN TB_EMPMST e2 ON ia.customer_employee_no = e2.employee_no -- 승인자 조인
         LEFT JOIN TB_EMPMST e3 ON ia.customer_installer_no = e3.employee_no -- 설치자 조인
         LEFT JOIN TB_CUSTMST c ON sc.common_code = c.item_code
         LEFT JOIN TB_WHMST w ON ir.customer_code = w.customer_code
         LEFT JOIN TB_INOUT_HIS ih ON ia.output_no = ih.inout_no