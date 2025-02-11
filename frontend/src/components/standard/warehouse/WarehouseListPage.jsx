import React from "react";
import { Table } from "@chakra-ui/react";

function WarehouseListPage({
  warehouse,
  setIsDetailDialogOpen,
  setSelectedWarehouseKey,
  index,
}) {
  return (
    <>
      <Table.Row
        key={warehouse.warehouseKey}
        onDoubleClick={() => {
          setIsDetailDialogOpen(true);
          setSelectedWarehouseKey(warehouse.warehouseKey);
        }}
        _hover={{ backgroundColor: "gray.200" }}
        bg={warehouse.warehouseActive ? "white" : "gray.100"}
        style={{
          cursor: "pointer",
        }}
      >
        <Table.Cell
          textAlign="center"
          verticalAlign="middle"
          style={{
            width: "90px", // 더 좁게 설정 가능
            minWidth: "90px",
            maxWidth: "90px",
          }}
        >
          {index + 1}
        </Table.Cell>
        <Table.Cell textAlign="center" verticalAlign="middle" width={"25%"}>
          {warehouse.warehouseName}
        </Table.Cell>
        <Table.Cell textAlign="center" verticalAlign="middle" width={"13%"}>
          {warehouse.customerName}
        </Table.Cell>
        <Table.Cell textAlign="center" verticalAlign="middle" width={"13%"}>
          {warehouse.employeeName}
        </Table.Cell>
        <Table.Cell textAlign="center" verticalAlign="middle" width={"13%"}>
          {warehouse.warehouseState}
        </Table.Cell>
        <Table.Cell textAlign="center" verticalAlign="middle" width={"10%"}>
          {warehouse.warehouseCity}
        </Table.Cell>
        <Table.Cell textAlign="center" verticalAlign="middle">
          {warehouse.warehouseTel}
        </Table.Cell>
      </Table.Row>
    </>
  );
}

export default WarehouseListPage;
