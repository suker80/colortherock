import { createSlice } from "@reduxjs/toolkit";
export const userSlice = createSlice({
  name: "user",
  initialState: {
    nickname: "",
    registrationId: "",
    email: "",
    fulfilled: false,
  },
  reducers: {
    // 로그인 진행중 상황
    setPendingLogin: (state, action) => {
      state.email = action.payload.email;
      state.registrationId = action.payload.registrationId;
      state.fulfilled = false;
    },
    // 미완료 로그인 진행
    setfulfilledLogin: (state, action) => {
<<<<<<< HEAD
      state.nickname = action.payload.nickname;
=======
      state.nickName = action.payload.nickname;
>>>>>>> d09182074696f4a40ad8d14f935f554e9da80254
      state.fulfilled = true;
    },
    // 로그인 완료
    setLogin: (state, action) => {
<<<<<<< HEAD
      state.nickname = action.payload.nickname;
=======
      state.nickName = action.payload.nickname;
>>>>>>> d09182074696f4a40ad8d14f935f554e9da80254
      state.email = action.payload.email;
      state.registrationId = action.payload.registrationId;
      state.fulfilled = true;
    },
    // 로그아웃
    LogOut: (state) => {
      state.nickname = "";
      state.email = "";
      state.registrationId = "";
      state.fulfilled = false;
    },
  },
});

export const { setPendingLogin, setfulfilledLogin, setLogin, LogOut } =
  userSlice.actions;

export default userSlice.reducer;
