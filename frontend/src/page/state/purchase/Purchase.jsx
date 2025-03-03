import React, { useContext, useEffect, useState } from "react";
import { Box, Button, Flex, Heading, HStack, Stack } from "@chakra-ui/react";
import { PurchaseList } from "../../../components/state/purchase/PurchaseList.jsx";
import { StateSideBar } from "../../../components/tool/sidebar/StateSideBar.jsx";
import { PurchaseDialog } from "../../../components/state/purchase/PurchaseDialog.jsx";
import { useSearchParams } from "react-router-dom";
import axios from "axios";
import { AuthenticationContext } from "../../../context/AuthenticationProvider.jsx";

export function Purchase() {
  const { company } = useContext(AuthenticationContext);
  // URL 쿼리 파라미터 관련 상태
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState(searchParams.get("state") || "all");
  // 데이터 및 페이지 관련 상태
  const [purchaseList, setPurchaseList] = useState([]);
  const [count, setCount] = useState(0);
  // 선택된 항목 관련 상태
  const [purchaseRequestKey, setPurchaseRequestKey] = useState(null);
  const [purchaseRequestData, setPurchaseRequestData] = useState(null); // 발주 데이터를 상위 컴포넌트에서 관리
  // 다이얼로그 관련 상태
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);

  // 구매 관리 리스트 가져오기
  useEffect(() => {
    const controller = new AbortController(); // AbortController -> 요청을 취소하는 컨트롤러

    axios
      .get("/api/purchase/list", {
        params: searchParams,
        signal: controller.signal,
      })
      .then((res) => {
        setPurchaseList(res.data.purchaseList);
        setCount(res.data.count);
      });

    return () => controller.abort();
  }, [searchParams]);

  // 구매 요청 후 리스트 업데이트
  const handleSave = () => {
    axios
      .get("/api/purchase/list")
      .then((res) => {
        setPurchaseList(res.data.purchaseList);
        setCount(res.data.count);
      })
      .catch((error) => {
        console.error("구매 목록 업데이트 중 오류 발생:", error);
      });
  };

  // 구매 승인 또는 반려 후 리스트 업데이트
  const handleUpdateList = () => {
    axios
      .get("/api/purchase/list", { params: searchParams })
      .then((res) => {
        setPurchaseList(res.data.purchaseList);
        setCount(res.data.count);
      })
      .catch((error) => {
        console.error("구매 목록 업데이트 중 오류 발생:", error);
      });
  };

  // 구매 요청 다이얼로그 열기
  const handlePurchaseRequestClick = () => {
    setIsAddDialogOpen(true);
    setIsDialogOpen(true);
  };

  // 구매 승인 다이얼로그 열기
  const handleViewClick = (key) => {
    const selectedData = purchaseList.find(
      (item) => item.purchaseRequestKey === key,
    );
    setPurchaseRequestKey(key);
    setPurchaseRequestData(selectedData); // 해당 발주 데이터를 설정
    setIsAddDialogOpen(false);
    setIsDialogOpen(true);
  };

  // 다이얼로그 닫기
  const handleDialogClose = () => {
    setIsDialogOpen(false);
  };

  return (
    <Box>
      <HStack align={"flex-start"} w={"100%"}>
        <StateSideBar />
        <Stack flex={1} p={5} pb={0}>
          <Heading size={"xl"} mb={3} p={2}>
            구매 / 설치 관리 {">"} 구매 관리
          </Heading>
          <PurchaseList
            purchaseList={purchaseList}
            onViewClick={handleViewClick}
            count={count}
            searchParams={searchParams}
            setSearchParams={setSearchParams}
            state={state}
          />
          {/* 구매 요청 버튼 (권한에 따라 제한) */}
          {company && // company 값이 존재하는지 확인하고 순서대로 실행
            !company.startsWith("CUS") && (
              <Flex justify="flex-end">
                <Button
                  size={"lg"}
                  mt={"-65px"}
                  onClick={handlePurchaseRequestClick}
                >
                  구매 요청
                </Button>
              </Flex>
            )}
          {/* 구매 다이얼로그 */}
          <PurchaseDialog
            isOpen={isDialogOpen}
            isAddDialogOpen={isAddDialogOpen}
            purchaseRequestKey={purchaseRequestKey}
            purchaseRequestData={purchaseRequestData} // 발주 데이터 전달
            onSave={handleSave}
            onClose={handleDialogClose}
            onUpdateList={handleUpdateList}
          />
        </Stack>
      </HStack>
    </Box>
  );
}
