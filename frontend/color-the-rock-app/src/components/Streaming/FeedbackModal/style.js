import styled from "styled-components";
import { VscChromeClose } from "react-icons/vsc";
import { FiRotateCcw } from "react-icons/fi";

export const ContainerWrap = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  background-color: black;
  opacity: 0.3;

  @media (min-width: 992px) {
    top: 0;
    left: 0;
    bottom: 0;
    right: 50%;
  }
`;

export const Container = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  /* background-color: transparent; */
`;

export const ContentBox = styled.div`
  width: calc(100% - 32px);
  width: 100%;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;

  /* #canvas {
    overflow: hidden;
    opacity: 0.7;
    width: 100%;
    height: 100%;
    z-index: 4000;
  } */
`;

export const Canvas = styled.canvas`
  overflow: hidden;
  opacity: 0.7;
  width: 100%;
  height: 100%;
  z-index: 4000;
`;

export const ChromeClose = styled(VscChromeClose)`
  position: absolute;
  right: 1rem;
  top: 1rem;
  font-size: 2rem;
  z-index: 5050;
`;

export const RotateCcw = styled(FiRotateCcw)`
  /* position: absolute;
  bottom: 1rem;
  right: 1rem; */
  z-index: 5001;
  /* padding: 1rem 0; */
  font-size: 2rem;
  margin: 1rem 0;
  opacity: 100%;
`;

export const Sticker = styled.img`
  position: absolute;
  left: ${(props) => props.x};
  right: ${(props) => props.y};
  width: 1rem;
  height: 1rem;
  border: 2px solid ${(props) => props.color};
  z-index: 5000;
`;

export const ButtonWrap = styled.div`
  position: absolute;
  bottom: 84px;
  right: 1rem;
  margin: 1rem 0;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 5000;
`;

export const Button = styled.span`
  font-size: 2rem;
  padding: 1rem 0;
  opacity: 100%;
`;
