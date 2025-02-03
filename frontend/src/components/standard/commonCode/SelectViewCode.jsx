import {
  SelectContent,
  SelectItem,
  SelectRoot,
  SelectTrigger,
  SelectValueText,
} from "@chakra-ui/react";
import React from "react";

export function SelectViewCode({ selectOptions, onChange, value }) {
  const handleSelect = (selectedItem) => {
    if (onChange) {
      onChange(selectedItem.value); // 선택된 값 전달
    }
  };

  return (
    <SelectRoot
      size="sm"
      width="150px"
      collection={selectOptions}
      position="relative"
      pt={"10px"}
      pb={"10px"}
      onValueChange={handleSelect}
    >
      <SelectTrigger>
        <SelectValueText
          placeholder={value === "SYSTEM" ? "시스템 코드" : "품목 코드"}
        />
      </SelectTrigger>
      <SelectContent
        style={{
          width: "150px",
          top: "50px",
          position: "absolute",
        }}
      >
        {selectOptions.items.map((option) => (
          <SelectItem item={option} key={option.value}>
            {option.label}
          </SelectItem>
        ))}
      </SelectContent>
    </SelectRoot>
  );
}
