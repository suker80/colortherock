import React from "react";
import * as S from "./style";
import TestImg from "../../../assets/img/intro/bg-intro.png";
const Thumbnail = ({ id, title, userNickname, gymName, imgUrl, isLive }) => {
  return (
    <S.Container id={id}>
      <S.ThumbnailImg src={!imgUrl ? TestImg : imgUrl} />
      <S.VideoText isLive={true}>{title}</S.VideoText>
      <S.VideoText isLive={isLive}>
        <S.LiveBadge />
        {userNickname}
      </S.VideoText>
      <S.Tag>{gymName}</S.Tag>
    </S.Container>
  );
};
export default Thumbnail;
