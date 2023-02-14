import React from "react";

export const Sticker = ({ x, y, imoji }) => {
  return (
    <span
      style={{
        position: "absolute",
        left: x - 20,
        top: y - 20,
        width: 50,
        height: 50,
        fontSize: "2.5rem",
      }}
    >
      {imoji}
    </span>
  );
};
