import React, { useState, createRef, useEffect, useRef } from "react";
import Sticker from "../Sticker";
import * as S from "./style";
import PropTypes from "prop-types";

let width;
let height;

const FeedbackModal = ({ closeFeedback, session, picture }) => {
  const [imoji, setImoji] = useState("✋🏻");

  const canvasRef = createRef(null);
  const parentRef = useRef(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    const parent = parentRef.current;
    canvas.width = parent.offsetWidth;
    canvas.height = parent.offsetHeight;
    width = canvas.width;
    height = canvas.height;
  }, []);

  const sendDrawing = (e) => {
    const data = {
      x: e.clientX,
      y: e.clientY,
      width: canvasRef.current.width,
      height: canvasRef.current.height,
      color: "#8ED6FF",
      imoji: imoji,
    };

    const signalOptions = {
      data: JSON.stringify(data),
      type: "drawingSignal",
      to: [],
    };
    session.signal(signalOptions);
  };

  const onClickReset = () => {
    const signalOptions = {
      type: "reset",
      to: [],
    };
    session.signal(signalOptions);
  };

  const changeImoji = (value) => {
    setImoji(value);
  };

  return (
    <S.ContainerWrap>
      <S.Container>
        <S.ContentBox ref={parentRef}>
          <S.ChromeClose
            onClick={() => {
              closeFeedback();
            }}
          />

          <S.ButtonWrap>
            <S.Button onClick={() => changeImoji("🤚🏻")}>🤚🏻</S.Button>
            <S.Button onClick={() => changeImoji("🦶🏻")}>🦶🏻</S.Button>
            <S.Button onClick={() => changeImoji("❌")}>❌</S.Button>
            <S.Button onClick={() => changeImoji("📌")}>📌</S.Button>
            <S.RotateCcw onClick={onClickReset} />
          </S.ButtonWrap>

          <canvas
            id="canvas"
            style={{
              width: "100%",
              height: "100%",
            }}
            ref={canvasRef}
            onPointerDown={sendDrawing}
          />
          {picture !== [] && picture.length > 0
            ? picture.map((item, idx) => (
                <Sticker
                  key={idx}
                  x={(item.x * width) / item.width}
                  y={(item.y * height) / item.height}
                  imoji={item.imoji}
                />
              ))
            : null}
        </S.ContentBox>
      </S.Container>
    </S.ContainerWrap>
  );
};

export default FeedbackModal;

FeedbackModal.propTypes = {
  closeFeedback: PropTypes.func.isRequired,
  session: PropTypes.object.isRequired,
  picture: PropTypes.array.isRequired,
};
