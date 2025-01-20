import React from "react";
import { Box, Center, createListCollection, Table } from "@chakra-ui/react";
import { Pagination } from "../tool/Pagination.jsx";
import { ActiveSwitch } from "../tool/ActiveSwitch.jsx";
import { SearchBar } from "../tool/SearchBar.jsx";
import { Sort } from "../tool/Sort.jsx";
import { Checkbox } from "../ui/checkbox.jsx";

export function ItemList({
  itemList,
  count,
  searchParams,
  setSearchParams,
  onRowClick,
}) {
  // 검색 옵션
  const itemSearchOptions = createListCollection({
    items: [
      { label: "전체", value: "all" },
      { label: "품목명", value: "itemName" },
      { label: "담당 업체", value: "customerName" },
      { label: "규격", value: "size" },
      { label: "단위", value: "unit" },
      { label: "입고가", value: "inputPrice" },
      { label: "출고가", value: "outputPrice" },
    ],
  });

  // 정렬 헤더
  const sortOptions = [
    { key: "itemKey", label: "#" },
    { key: "itemCommonName", label: "품목명" },
    { key: "customerName", label: "담당업체" },
    { key: "size", label: "규격" },
    { key: "unit", label: "단위" },
    { key: "inputPrice", label: "입고가" },
    { key: "outputPrice", label: "출고가" },
  ];

  // active가 true인 경우 사용 여부 컬럼 추가
  const active = searchParams.get("active") === "true";
  const dynamicSortOptions = active
    ? [...sortOptions, { key: "itemActive", label: "사용 여부" }]
    : sortOptions;

  return (
    <Box px={10}>
      <SearchBar
        itemSearchOptions={itemSearchOptions}
        onSearchChange={(nextSearchParam) => setSearchParams(nextSearchParam)}
      />
      <ActiveSwitch
        onActiveChange={(nextSearchParam) => setSearchParams(nextSearchParam)}
      />
      <Box>
        <Table.Root>
          <Table.Header>
            <Table.Row>
              <Sort
                sortOptions={dynamicSortOptions}
                onSortChange={(nextSearchParam) =>
                  setSearchParams(nextSearchParam)
                }
              />
            </Table.Row>
          </Table.Header>
          <Table.Body>
            {itemList?.map((item, index) => (
              <Table.Row
                key={item.itemKey}
                onClick={() => onRowClick(item.itemKey)}
                style={{ cursor: "pointer" }}
                _hover={{ backgroundColor: "gray.100" }}
              >
                <Table.Cell textAlign="center"> {index + 1}</Table.Cell>
                <Table.Cell textAlign="center">
                  {item.itemCommonName}
                </Table.Cell>
                <Table.Cell textAlign="center">{item.customerName}</Table.Cell>
                <Table.Cell textAlign="center">{item.size}</Table.Cell>
                <Table.Cell textAlign="center">{item.unit}</Table.Cell>
                <Table.Cell textAlign="center">{item.inputPrice}</Table.Cell>
                <Table.Cell textAlign="center">{item.outputPrice}</Table.Cell>
                {active && (
                  <Table.Cell textAlign="center">
                    <Checkbox checked={item.itemActive} />
                  </Table.Cell>
                )}
              </Table.Row>
            ))}
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
