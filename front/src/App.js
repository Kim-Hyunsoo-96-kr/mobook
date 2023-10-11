import './App.css';
import {BrowserRouter, Navigate, Route, Routes, useNavigate} from "react-router-dom";
import Main from "./pages/Main";
import NavBar from "./comp/NavBar";
import Login from "./pages/Login";
import Logout from "./pages/Logout";
import Test from "./pages/Test";
import Search from "./pages/Search";
import AddBook from "./pages/AddBook";
import MyRentBook from "./pages/MyRentBook";
import MyBookLog from "./pages/MyBookLog";
import MyRecommendBook from "./pages/MyRecommendBook";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector,
    loginedUserInfoAtom,
    nav, needToRefreshAccessToken,
    needToRefreshRefreshToken,
    setLogin, setLogout
} from "./recoil";
import {useEffect, useState} from "react";
import {useRecoilState, useRecoilValue} from "recoil";
import axios from "axios";
import Request from "./pages/Request";
import MyRequestBook from "./pages/MyRequestBook";
import PasswordChange from "./pages/PasswordChange";
import FindPassword from "./pages/FindPassword";
import CreateAccount from "./pages/CreateAccount";
import AdminBookLog from "./pages/AdminBookLog";
import AdminRequestBook from "./pages/AdminRequestBook";
import AdminRentBook from "./pages/AdminRentBook";
import Notice from "./pages/Notice";
import ToastUI from "./pages/NoticeAdd";
import NoticeAdd from "./pages/NoticeAdd";
import NoticeDetail from "./pages/NoticeDetail";
import NoticeEdit from "./pages/NoticeEdit";

function App() {
    // const navigate = useNavigate()
    const [isReady, setIsReady] = useState(false);
    const [loginedUserInfo, setLoginedUserInfo] = useRecoilState(
        loginedUserInfoAtom
    );
    const isLogined = useRecoilValue(isLoginedSelector);

    useEffect(() => {
        const axiosInstanceRequestInterceptor = axiosInstance.interceptors.request.use(
            async function (config) {
                if (!config.headers) {
                    config.headers = {};
                }

                // 보내는 데이터가 json 이라고 알림
                config.headers.Accept = "application/json";

                // 로그인이 안되어 있다면, 헤더에 아무 작업도 하지 않아야 함으로 바로 리턴
                if (loginedUserInfo == null) {
                    return config;
                }

                // 리프레시 토큰 재발급 필요한지 체크
                const needToRefreshRefreshToken_ = needToRefreshRefreshToken(
                    loginedUserInfo.refreshToken
                );

                // 엑세스 토큰 재발급 필요한지 체크
                const needToRefreshAccessToken_ = needToRefreshAccessToken(
                    loginedUserInfo.accessToken
                );


                // 엑세스 토큰과 리프레시 토큰이 둘다 유효하다면, 헤더에 엑세스 토큰 추가 하여 리턴
                if (
                    needToRefreshAccessToken_ == false &&
                    needToRefreshRefreshToken_ == false
                ) {
                    config.headers.Authorization = `Bearer ${loginedUserInfo.accessToken}`;

                    return config;
                }
                // 리프레시 토큰 갱신
                if (needToRefreshRefreshToken_) {
                    const response = await axios.post(CONFIG.API_LOGOUT,
                        {
                            refreshToken: loginedUserInfo.refreshToken
                        });

                    setLogout(setLoginedUserInfo)
                }

                // 엑세스 토큰 갱신
                if (needToRefreshAccessToken_) {
                    try {
                        const response = await axios.post(
                            CONFIG.API_REFRESH_TOKEN,
                            {
                                refreshToken: loginedUserInfo.refreshToken
                            },
                            {
                                Accept: "application/json"
                            }
                        );

                        setLogin(
                            setLoginedUserInfo,
                            response.data.accessToken,
                            loginedUserInfo.refreshToken
                        );

                        config.headers.Authorization = `Bearer ${response.data.accessToken}`;

                        return config;
                    } catch (e) {
                        // 여기서 setLogout(setLoginedUserInfo); 이렇게 희안하게 잘 안된다.
                        // 그래서 아래와 같이 부드러운 방식으로 로그아웃 한다.
                        Navigate("/logout", { replace: true });
                        // 이렇게 하면 이후에 실행될 통신이 실행되지 않는다.
                        // 그리고 그렇게 하는게 맞다.
                        // 여기까지 왔다는 것은 토큰이 잘못 되었다는 뜻이니까.
                        return Promise.reject(e);
                    }
                }

                return config;
            }
        );

        // 이 인터셉터는 응답에 대한 것이라서, 현재는 사용하지 않는다.
        const axiosInstanceResponseInterceptor = axiosInstance.interceptors.response.use(
            function (response) {
                return response;
            }
        );

        setIsReady(true);

        return () => {
            // 이 부분은 메모리 누수를 방지하기 위해
            // 또, 인터셉터가 중복해서 들어가는 것을 막기위해 꼭 필요하다.
            axiosInstance.interceptors.request.eject(axiosInstanceRequestInterceptor);
            axiosInstance.interceptors.response.eject(
                axiosInstanceResponseInterceptor
            );
            setIsReady(false);
        };
    }, [loginedUserInfo]); // loginedUserInfo 변수 중 둘 중 하나라도 값이 바뀌면 useEffect 를 다시 실행한다는 뜻 이다.

    if (!isReady) {
        return <div class="loading-1">앱 로딩중</div>;
    }
  return (
      <BrowserRouter>
          <div>
          <NavBar nav={nav}/>
          <Routes>
              <Route path="/" element={<Main />} />
              <Route path="/login" element={<Login />} />
              <Route path="/logout" element={<Logout />} />
              <Route path="/passwordChange" element={<PasswordChange />} />
              <Route path="/findPassword" element={<FindPassword />} />
              <Route path="/search" element={<Search />} />
              <Route path="/add" element={<AddBook />} />
              <Route path="/test" element={<Test />} />
              <Route path="/myBookLog" element={<MyBookLog />} />
              <Route path="/myRentBook" element={<MyRentBook />} />
              <Route path="/myRecommendBook" element={<MyRecommendBook />} />
              <Route path="/myRequestBook" element={<MyRequestBook />} />
              <Route path="/request" element={<Request />} />
              <Route path="/createAccount" element={<CreateAccount />} />
              <Route path="/adminBookLog" element={<AdminBookLog />} />
              <Route path="/adminRequestBook" element={<AdminRequestBook />} />
              <Route path="/adminRentBook" element={<AdminRentBook />} />
              <Route path="/notice" element={<Notice />} />
              <Route path="/noticeAdd" element={<NoticeAdd />} />
              <Route path="/notice/detail/:noticeId" element={<NoticeDetail />} />
              <Route path="/notice/edit/:noticeId" element={<NoticeEdit />} />
          </Routes>
          </div>
      </BrowserRouter>
  );
}

export default App;
