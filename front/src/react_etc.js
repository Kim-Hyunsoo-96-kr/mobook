console.clear();

// 리액트 기본
import React, { useState, useEffect } from "https://cdn.skypack.dev/react";
import ReactDOM from "https://cdn.skypack.dev/react-dom";

// 리액트 리코일(상태관리, 쉽게 말하면 전역변수 관리, 컴포넌트간 데이터 공유에 필요)
import {
    RecoilRoot,
    atom,
    useRecoilState,
    useSetRecoilState,
    useRecoilValue,
    selector
} from "https://cdn.skypack.dev/recoil";

// 리액트 리코일 퍼시스트(전역변수 중 일부를 영속적으로 로컬 스토리지에 저장, 로그인 유지에 쓰임)
import { recoilPersist } from "https://cdn.skypack.dev/recoil-persist";
const { persistAtom } = recoilPersist();

// 리액트 라우터(화면 이동)
import {
    HashRouter as Router,
    Routes,
    Route,
    NavLink,
    Navigate,
    useParams,
    useNavigate
} from "https://cdn.skypack.dev/react-router-dom";

// 리액트 쿼리(useEffect 없이 API 통신 가능하도록, API 통신 쉽게 해줌)
import {
    QueryClient,
    QueryClientProvider,
    useQuery
} from "https://cdn.skypack.dev/react-query@3";

const queryClient = new QueryClient();

// 액시오스(통신 API, fetch 를 써도 되지만, access_key, refresh_key 관련 인터셉터 기능을 사용하기 위해)
import axios from "https://cdn.skypack.dev/axios";
const axiosInstance = axios.create();

// 클래스네임스(className의 값을 동적으로 바꿀 때 편함)
import classnames from "https://cdn.skypack.dev/classnames";

// lib 시작
// 일반 lib 시작
// 숫장에 콤마(,) 붙이기
function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 어떠한 시각이 현재로 부터 몇초 남았는지
function secondsDiffFromNow(exp) {
    const diffMillis = parseInt(exp + "000") - new Date().getTime();
    return parseInt(diffMillis / 1000);
}
// 일반 lib 끝

// jwt lib 시작

// 토큰에서 페이로드(데이터) 부분 가져오기
function getPayloadFromJWT(token) {
    const base64Payload = token.split(".")[1];
    return JSON.parse(atob(base64Payload));
}

// 토큰의 페이로드 부분에서 만료시간 가져오기
function getPayloadFromJWTExp(token) {
    const base64Payload = atob(token.split(".")[1]);
    return base64Payload.split('"exp":')[1].split(',"')[0];
}

// 엑세스 토큰을 재발급(리프레시) 해야하는지 체크
function needToRefreshAccessToken(token) {
    const exp = getPayloadFromJWTExp(token);
    return secondsDiffFromNow(exp) < 60 * 0;
}

// 리프레시 토큰을 재발급(리프레시) 해야하는지 체크
function needToRefreshRefreshToken(token) {
    const exp = getPayloadFromJWTExp(token);
    return secondsDiffFromNow(exp) < 60 * 60 * 24 * 10;
}
// jwt lib 끝
// lib 시작

// config 시작
const CONFIG = {};

// API 주소, BASE
CONFIG.MBLY_API_BASE = "http://localhost:8000";

// API 주소, 로그인
CONFIG.MBLY_API_LOGIN = `${CONFIG.MBLY_API_BASE}/accounts/api/token/`;
// API 주소, 리프레시 토큰 재발급
CONFIG.MBLY_API_REFRESH_REFRESH_TOKEN = `${CONFIG.MBLY_API_LOGIN}refresh/refresh_token/`;
// API 주소, 엑세스토큰 재발급
CONFIG.MBLY_API_REFRESH_ACCESS_TOKEN = `${CONFIG.MBLY_API_LOGIN}refresh/access_token/`;

// API 주소, 관리자용 상품리스트
CONFIG.MBLY_ADMIN_API_PRODUCT_LIST_BASE = `${CONFIG.MBLY_API_BASE}/products/market_api/`;
// API 주소, 관리자용 상세페이지
CONFIG.MBLY_ADMIN_API_PRODUCT_DETAIL_BASE =
    CONFIG.MBLY_ADMIN_API_PRODUCT_LIST_BASE;
// API 주소, 관리자용 상품삭제
CONFIG.MBLY_ADMIN_API_PRODUCT_DELETE_BASE =
    CONFIG.MBLY_ADMIN_API_PRODUCT_LIST_BASE;

// config 끝

// 리코일 atom, selector 시작
// 리코일 atom와 selector는 전역변수 정도로 해석하면 됩니다.

// 로그인한 회원정보(raw)
const loginedUserInfoAtom = atom({
    key: "app/loginedUserInfoAtom", // 이 키는 나중에 디버깅시에 의미가 있음
    default: null, // 기본값
    effects_UNSTABLE: [persistAtom] // 이 변수의 값은 로컬 스토리지에 영속적으로 저장, 이렇게 해야 F5 키 눌러도 로그인 유지 가능
});

// loginedUserInfoAtom 를 기초로 현재 로그인 했는지 알려주는 변수
const isLoginedSelector = selector({
    key: "app/isLoginedSelector",
    get: ({ get }) => get(loginedUserInfoAtom) != null
});

// loginedUserInfoAtom 를 사용하기 좋게 살짝 가공한 버전(앱에서는 이걸 사용한다.)
const loginedUserInfoSelector = selector({
    key: "app/loginedUserInfoSelector",
    get: ({ get }) => {
        const isLogined = get(isLoginedSelector);

        if (!isLogined) return null; // 로그인 안했다면 null 반환

        const rawLoginedUserInfo = get(loginedUserInfoAtom);

        return {
            ...rawLoginedUserInfo,
            profileImgUrl: `${CONFIG.MBLY_API_BASE}${rawLoginedUserInfo.profile_img_url}` // 이렇게 해줘야 앱에서 이미지 보여줄 때 편함
        };
    }
});

// 전역 비지니스 로직
function setLogin(setLoginedUserInfo, accessToken, refreshToken) {
    const userInfo = getPayloadFromJWT(accessToken);
    userInfo.accessToken = accessToken; // 이 토큰과
    userInfo.refreshToken = refreshToken; // 이 토큰은 꼭 있어야 로그인 했다고 인식됨
    setLoginedUserInfo(userInfo);
}

function setLogout(setLoginedUserInfo) {
    setLoginedUserInfo(null);
}

// 컴포넌트 시작
const App = () => {
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
                    try {
                        const response = await axios.post(
                            CONFIG.MBLY_API_REFRESH_REFRESH_TOKEN,
                            {
                                refresh: loginedUserInfo.refreshToken
                            },
                            {
                                Accept: "application/json"
                            }
                        );

                        setLogin(
                            setLoginedUserInfo,
                            response.data.access,
                            response.data.refresh
                        );

                        config.headers.Authorization = `Bearer ${response.data.access}`;

                        return config;
                    } catch (e) {
                        // 여기서 setLogout(setLoginedUserInfo); 이렇게 희안하게 잘 안된다.
                        // 그래서 아래와 같이 부드러운 방식으로 로그아웃 한다.
                        navigate("/accounts/logout", { replace: true });
                        // 이렇게 하면 이후에 실행될 통신이 실행되지 않는다.
                        // 그리고 그렇게 하는게 맞다.
                        // 여기까지 왔다는 것은 토큰이 잘못 되었다는 뜻이니까.
                        return Promise.reject(error);
                    }
                }

                // 엑세스 토큰 갱신
                if (needToRefreshAccessToken_) {
                    try {
                        const response = await axios.post(
                            CONFIG.MBLY_API_REFRESH_ACCESS_TOKEN,
                            {
                                refresh: loginedUserInfo.refreshToken
                            },
                            {
                                Accept: "application/json"
                            }
                        );

                        setLogin(
                            setLoginedUserInfo,
                            response.data.access,
                            loginedUserInfo.refreshToken
                        );

                        config.headers.Authorization = `Bearer ${response.data.access}`;

                        return config;
                    } catch (e) {
                        // 여기서 setLogout(setLoginedUserInfo); 이렇게 희안하게 잘 안된다.
                        // 그래서 아래와 같이 부드러운 방식으로 로그아웃 한다.
                        navigate("/accounts/logout", { replace: true });
                        // 이렇게 하면 이후에 실행될 통신이 실행되지 않는다.
                        // 그리고 그렇게 하는게 맞다.
                        // 여기까지 왔다는 것은 토큰이 잘못 되었다는 뜻이니까.
                        return Promise.reject(error);
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
        <>
            <Header />
            <Routes>
                <Route path="/products/" element={<ProductsListPage />} />
                <Route path="/products/:id" element={<ProductsDetailPage />} />

                <Route path="/accounts/profile" element={<AccountsProfilePage />} />
                <Route path="/accounts/login" element={<AccountsLoginPage />} />
                <Route path="/accounts/logout" element={<AccountsLogoutPage />} />

                <Route path="/home/main" element={<HomeMainPage />} />
                <Route path="/home/about" element={<HomeInfoPage />} />

                <Route path="*" element={<Navigate to="/home/main" />} />
            </Routes>
        </>
    );
};

const Header = () => {
    const isLogined = useRecoilValue(isLoginedSelector);
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);

    return (
        <>
            <header className="top-bar fixed top-0 left-0 w-full z-50 h-10 shadow text-gray-500 bg-white">
                <div className="container mx-auto h-full flex">
                    <NavLink to="/home/main" className="flex items-center px-4">
                        <i className="fab fa-pied-piper-hat"></i>
                    </NavLink>
                    <div className="flex-grow"></div>
                    <nav className="menu-1">
                        <ul className="flex h-full">
                            <HeaderMenu1Item to="/home/main">
                                <i className="fas fa-home"></i>
                                <span className="hidden sm:block">&nbsp;</span>
                                <span className="hidden sm:block">홈</span>
                            </HeaderMenu1Item>
                            <HeaderMenu1Item to="/home/about">
                                <i className="fas fa-info"></i>
                                <span className="hidden sm:block">&nbsp;</span>
                                <span className="hidden sm:block">앱 정보</span>
                            </HeaderMenu1Item>

                            {isLogined && (
                                <>
                                    <HeaderMenu1Item to="/accounts/profile">
                                        <img
                                            src={loginedUserInfo.profileImgUrl}
                                            className="w-6 rounded-full"
                                        />
                                        <span className="hidden sm:block">&nbsp;</span>
                                        <span className="hidden sm:block">
                      {loginedUserInfo.name}
                    </span>
                                    </HeaderMenu1Item>
                                    <HeaderMenu1Item
                                        to="/products"
                                        isActive={(match) => {
                                            return match.path.startsWith("/products/");
                                        }}
                                    >
                                        <i className="fas fa-tshirt"></i>
                                        <span className="hidden sm:block">&nbsp;</span>
                                        <span className="hidden sm:block">상품</span>
                                    </HeaderMenu1Item>
                                    <HeaderMenu1Item to="/accounts/logout">
                                        <i className="fas fa-sign-out-alt"></i>
                                        <span className="hidden sm:block">&nbsp;</span>
                                        <span className="hidden sm:block">로그아웃</span>
                                    </HeaderMenu1Item>
                                </>
                            )}
                            {isLogined || (
                                <>
                                    <HeaderMenu1Item to="/accounts/login">
                                        <i className="fas fa-sign-in-alt"></i>
                                        <span className="hidden sm:block">&nbsp;</span>
                                        <span className="hidden sm:block">로그인</span>
                                    </HeaderMenu1Item>
                                </>
                            )}
                        </ul>
                    </nav>
                </div>
            </header>
            <div className="h-10"></div>
        </>
    );
};

const HeaderMenu1Item = ({ to, children, isActive }) => {
    return (
        <li>
            <NavLink
                isActive={isActive}
                className={({ isActive }) =>
                    classnames(
                        "h-full",
                        "px-2",
                        "flex",
                        "items-center",
                        "justify-center",
                        { "text-red-500": isActive }
                    )
                }
                to={to}
            >
                {children}
            </NavLink>
        </li>
    );
};

const HomeMainPage = () => {
    return (
        <section>
            <div className="container mx-auto flex justify-center items-center">
                <span>오늘 총 매출 : 234,123,432원</span>
            </div>
        </section>
    );
};

const ProductsListPage = () => {
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부

    if (!isLogined) return <Navigate to="/" replace />; // 로그인 안했다면 메인화면으로 보냄

    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);

    const { isLoading, error, data } = useQuery("productList", async () => {
        // productList 는 캐싱키, 참고로 지금은 캐시 사용 안함
        // 참고로 axiosInstance 로 요청하면 알아서 엑세스 키가 헤더에 붙어서 요청됩니다.
        // 그것은 액시오스 인터셉터에서 자동으로 해줍니다.
        // 우리가 App 함수에서 그렇게 세팅 했습니다.
        const response = await axiosInstance.get(
            `${CONFIG.MBLY_ADMIN_API_PRODUCT_LIST_BASE}${loginedUserInfo.master_market_id}/`
        );

        return response.data;
    });

    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }

    return (
        <section>
            <div className="container mx-auto flex justify-center items-center">
                <div className="p-4">
                    <ul className="grid sm:grid-cols-2 md:grid-cols-3 lg:sm:grid-cols-4 gap-[20px] mt-3">
                        {data.results.map((product) => (
                            <li key={product.id} className="flex flex-col group">
                                <NavLink
                                    to={`/products/${product.id}`}
                                    className="relative overflow-hidden rounded"
                                >
                                    <img
                                        className="block w-full transition-all group-hover:scale-110 object-cover"
                                        src={product.thumb_img_url}
                                        alt=""
                                        style={{ "aspect-ratio": "1 / 1" }}
                                    />
                                    <div className="absolute inset-0 bg-[#00000000] group-hover:bg-[#00000055] transition-all"></div>
                                    <div className="absolute inset-0 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-all">
                    <span className="text-white border-2 border-white border-solid p-2 rounded whitespace-nowrap">
                      VIEW MORE
                    </span>
                                    </div>
                                </NavLink>

                                <NavLink
                                    to={`/products/${product.id}`}
                                    className="text-center mt-2"
                                >
                  <span className="badge bg-primary">
                    {product.market.name} 평점{" "}
                      {product.market.review_point.toFixed(2)}
                  </span>
                                </NavLink>

                                <NavLink
                                    to={`/products/${product.id}`}
                                    className="text-center mt-2 no-underline text-black italic group-hover:underline"
                                >
                                    {product.display_name}
                                </NavLink>

                                <NavLink
                                    to={`/products/${product.id}`}
                                    className="text-center mt-2 no-underline group-hover:t-text-blue-500"
                                >
                                    <span>{numberWithCommas(product.sale_price)}원</span>
                                    <span>&nbsp;</span>
                                    <span className="line-through text-gray-400">
                    {numberWithCommas(product.price)}원
                  </span>
                                </NavLink>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </section>
    );
};

const ProductsDetailPage = () => {
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부

    if (!isLogined) return <Navigate to="/" replace />; // 로그인 안했다면 메인화면으로 보냄

    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);

    const navigate = useNavigate();
    const { id } = useParams(); // URL에서 id 얻어오기

    const { isLoading, error, data } = useQuery(
        `productDetail__${id}`,
        async () => {
            const response = await axiosInstance.get(
                `${CONFIG.MBLY_ADMIN_API_PRODUCT_DETAIL_BASE}${loginedUserInfo.master_market_id}/${id}/`
            );

            return response.data;
        }
    );

    const removeProduct = async () => {
        if (!confirm("정말 삭제하시겠습니까?")) {
            return;
        }

        const response = await axiosInstance.delete(
            `${CONFIG.MBLY_ADMIN_API_PRODUCT_DELETE_BASE}${loginedUserInfo.master_market_id}/${id}/`
        );
        navigate("/products", { replace: true }); // 삭제 시도 후 성공했다면 리스트로 이동
        alert("상품이 삭제되었습니다."); // 이 코드를 navigate 전에 쓰지 마세요. 후에 쓰세요.
    };

    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }

    return (
        <section>
            <div className="container mx-auto flex justify-center items-center">
                <div className="p-4">
                    <h1>
                        <span className="badge badge-primary">{data.id}번</span>
                        &nbsp;
                        <span className="badge badge-info">
              {data.display_name} 상세페이지
            </span>
                    </h1>

                    <div className="mt-3">
                        <button className="btn btn-sm btn-primary" onClick={removeProduct}>
                            삭제
                        </button>
                        &nbsp;
                        <button
                            className="btn btn-sm btn-primary btn-outline"
                            onClick={() => navigate(-1)}
                        >
                            뒤로가기
                        </button>
                    </div>

                    <img
                        className="block w-full rounded mt-3"
                        src={data.thumb_img_url}
                        alt=""
                    />
                </div>
            </div>
        </section>
    );
};

const HomeInfoPage = () => {
    return (
        <section>
            <div className="container mx-auto flex justify-center items-center">
                <span>멋블리 마켓 관리자 페이지입니다.</span>
            </div>
        </section>
    );
};

const AccountsProfilePage = () => {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const isLogined = useRecoilValue(isLoginedSelector);

    if (!isLogined)
        return <Navigate to="/accounts/login/?next=/accounts/profile" replace />;

    return (
        <section>
            <div className="container mx-auto flex justify-center items-center">
                <div className="flex-grow max-w-md p-4">
                    <h1 className="font-bold text-lg">사용자 프로필</h1>
                    <table className="table-1">
                        <tbody>
                        <tr>
                            <th>
                                <span className="badge">이미지</span>
                            </th>
                            <td>
                                <img
                                    className="w-20 rounded-full"
                                    src={loginedUserInfo.profileImgUrl}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <span className="badge">닉네임</span>
                            </th>
                            <td>{loginedUserInfo.name}</td>
                        </tr>
                        <tr>
                            <th>
                                <span className="badge">이메일</span>
                            </th>
                            <td>{loginedUserInfo.email || "-"}</td>
                        </tr>
                        <tr>
                            <th>
                                <span className="badge">관리자</span>
                            </th>
                            <td>{loginedUserInfo.is_active ? "슈퍼관리자" : "-"}</td>
                        </tr>
                        <tr>
                            <th>
                                <span className="badge">성별</span>
                            </th>
                            <td>{loginedUserInfo.gender_display}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    );
};

const AccountsLoginPage = () => {
    const setLoginedUserInfo = useSetRecoilState(loginedUserInfoAtom);
    const isLogined = useRecoilValue(isLoginedSelector);

    if (isLogined) return <Navigate to="/" replace />;

    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.username.value = form.username.value.trim();
        form.password.value = form.password.value.trim();

        if (form.username.value.length == 0) {
            alert("username을 입력해주세요.");
            form.username.focus();
            return;
        }

        if (form.password.value.length == 0) {
            alert("password를 입력해주세요.");
            form.password.focus();
            return;
        }

        const username = form.username.value;
        const password = form.password.value;

        try {
            const response = await axios.post(CONFIG.MBLY_API_LOGIN, {
                username,
                password
            });

            setLogin(setLoginedUserInfo, response.data.access, response.data.refresh);
        } catch (e) {
            console.log(e);

            if (e.response.status == 401) {
                alert("아이디 또는 비밀번호가 일치하지 않습니다.");
            } else {
                alert(e.message);
            }

            form.password.focus();
        }
    };

    return (
        <section>
            <div className="container mx-auto flex justify-center items-center">
                <div className="flex-grow max-w-md p-4">
                    <h1 className="font-bold text-lg">로그인</h1>
                    <form onSubmit={onSubmit}>
                        <div className="form-control">
                            <label className="label">
                                <span className="label-text">로그인아이디</span>
                            </label>
                            <input
                                type="text"
                                name="username"
                                placeholder="로그인아이디"
                                className="input input-bordered"
                                maxlength="20"
                            />
                        </div>

                        <div className="form-control">
                            <label className="label">
                                <span className="label-text">로그인비밀번호</span>
                            </label>
                            <input
                                type="password"
                                name="password"
                                placeholder="로그인비밀번호"
                                className="input input-bordered"
                                maxlength="20"
                            />
                        </div>

                        <div className="mt-3 grid grid-flow-col auto-cols-fr gap-2 px-1">
                            <input className="btn btn-primary" type="submit" value="로그인" />
                            <a href="/member/join" className="btn btn-secondary btn-outline">
                                가입하기
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    );
};

const AccountsLogoutPage = () => {
    const setLoginedUserInfo = useSetRecoilState(loginedUserInfoAtom);

    setLogout(setLoginedUserInfo);

    return <Navigate to="/" replace />;
};

const Root = () => {
    return (
        <RecoilRoot>
            <QueryClientProvider client={queryClient}>
                <Router>
                    <App />
                </Router>
            </QueryClientProvider>
        </RecoilRoot>
    );
};

ReactDOM.render(<Root />, document.getElementById("root"));
