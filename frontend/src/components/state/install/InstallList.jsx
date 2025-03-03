import React from "react";
import { Box, Center, createListCollection, Table } from "@chakra-ui/react";
import { SearchBar } from "../../tool/form/SearchBar.jsx";
import { Sort } from "../../tool/form/Sort.jsx";
import { Pagination } from "../../tool/form/Pagination.jsx";
import { StateRadioGroup } from "../../tool/form/StateRadioGroup.jsx";

export function InstallList({
  installList,
  onRowClick,
  count,
  searchParams,
  setSearchParams,
}) {
  // 정렬 헤더
  const sortInstallOptions = [
    { key: "installRequestKey", label: "#" },
    { key: "franchiseName", label: "가맹점" },
    { key: "itemCommonName", label: "품목" },
    { key: "customerName", label: "담당 업체" },
    { key: "outputNo", label: "출고 번호" },
    { key: "businessEmployeeName", label: "요청자" },
    { key: "customerEmployeeName", label: "반려/승인자" },
    { key: "customerInstallerName", label: "설치 기사" },
    { key: "warehouseName", label: "창고" },
    { key: "installDate", label: "날짜" },
  ];

  // 검색 옵션
  const searchOptions = createListCollection({
    items: [
      { label: "전체", value: "all" },
      { label: "가맹점", value: "franchiseName" },
      { label: "품목", value: "itemCommonName" },
      { label: "담당 업체", value: "customerName" },
      { label: "출고 번호", value: "outputNo" },
      { label: "요청자", value: "businessEmployeeName" },
      { label: "반려/승인자", value: "customerEmployeeName" },
      { label: "설치 기사", value: "customerInstallerName" },
      { label: "창고", value: "warehouseName" },
    ],
  });

  const radioOptions = [
    { value: "all", label: "전체" },
    { value: "request", label: "대기" },
    { value: "approve", label: "승인" },
    { value: "configuration", label: "완료" },
    { value: "disapprove", label: "반려" },
  ];

  return (
    <Box>
      <SearchBar
        searchOptions={searchOptions}
        onSearchChange={(nextSearchParam) => setSearchParams(nextSearchParam)}
      />
      <StateRadioGroup
        radioOptions={radioOptions}
        onRadioChange={(nextSearchParam) => setSearchParams(nextSearchParam)}
      />
      <Box>
        <Table.Root>
          <Table.Header>
            <Table.Row whiteSpace={"nowrap"} bg={"gray.100"}>
              <Sort
                sortOptions={sortInstallOptions}
                onSortChange={(nextSearchParam) =>
                  setSearchParams(nextSearchParam)
                }
                defaultSortKey={"installDate"}
              />
              <Table.Cell textAlign="center">상태</Table.Cell>
            </Table.Row>
          </Table.Header>
          <Table.Body>
            {installList.length > 0 ? (
              installList?.map((install, index) => (
                <Table.Row
                  key={index}
                  onDoubleClick={() => onRowClick(install)}
                  style={{
                    cursor: "pointer",
                  }}
                  _hover={{ backgroundColor: "gray.200" }}
                >
                  <Table.Cell textAlign="center" width="90px">
                    {index + 1}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="13%">
                    {install.franchiseName}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="10%">
                    {install.itemCommonName}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="10%">
                    {install.customerName}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="10%">
                    {install.outputNo || "-"}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="8%">
                    {install.businessEmployeeName}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="8%">
                    {install.customerEmployeeName || "-"}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="8%">
                    {install.customerInstallerName || "-"}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="10%">
                    {install.warehouseName}
                  </Table.Cell>
                  <Table.Cell textAlign="center" width="10%">
                    {install.installDate}
                  </Table.Cell>
                  <Table.Cell textAlign="center">{install.state}</Table.Cell>
                </Table.Row>
              ))
            ) : (
              <Table.Row>
                <Table.Cell textAlign="center" colSpan="11">
                  정보가 존재하지 않습니다.
                </Table.Cell>
              </Table.Row>
            )}
          </Table.Body>
        </Table.Root>
        <Center>
          <Pagination
            count={count}
            pageSize={10}
            onPageChange={(newPage) => {
              const nextSearchParam = new URLSearchParams(searchParams);
              nextSearchParam.set("page", newPage);
              setSearchParams(nextSearchParam);
            }}
          />
        </Center>
      </Box>
    </Box>
  );
}
