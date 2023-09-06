import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Link, Navigate, useLocation, useNavigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Pagination from "react-js-pagination";

const Notice = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["notice", page], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_NOTICE}?page=${page}`);
        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const handlePageChange = (page) => {
        navigate(`/notice?page=${page}`);
    };
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }


    return (
        <section class="bg-light py-5">
            <div class="container px-5 my-5">
                <div class="text-center mb-5">
                    <h1 class="fw-bolder">공지사항</h1>
                </div>
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3 d-flex justify-content-between">
                                    <span class="text-muted fs-4">공지사항</span>
                                    {isAdmin &&
                                        <Link to='/noticeAdd' class='btn btn-sm btn-primary'>글 쓰기</Link>
                                    }
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">등록일</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {data.noticeList.map((notice)=>(
                                            <tr>
                                                <td className="text-align-center">{notice.noticeId}</td>
                                                <td>
                                                    <Link to={`/noticeDetail/${notice.noticeId}`}>{notice.title}</Link>
                                                </td>
                                                <td className="text-align-center">{notice.editDate}</td>
                                                <td></td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                                <div className="p">
                                    <Pagination
                                        activePage={page+1}
                                        itemsCountPerPage={10}
                                        totalItemsCount={data.totalCnt}
                                        pageRangeDisplayed={5}
                                        onChange={handlePageChange}>
                                    </Pagination>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default Notice;
