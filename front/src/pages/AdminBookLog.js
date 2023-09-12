import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Pagination from "react-js-pagination";
import Swal from "sweetalert2";

const AdminBookLog = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["adminBook", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_ADMIN_BOOKLOG}?page=${page}&searchText=${searchText}`);
        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
    if(!isAdmin) {
        Swal.fire(
            '관리자만 접근할 수 있는 페이지입니다.',
            '이 에러가 반복되면 송주환 사원에게 문의해주세요.',
            'warning'
        )
        return <Navigate to={"/"}/>;
    }
    const handlePageChange = (page) => {
        navigate(`/adminBookLog?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/adminBookLog?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/adminBookLog?searchText=${searchText}&page=1`);
        }
    };
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
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
                    <h1 class="fw-bolder">전체 대여/반납 기록</h1>
                </div>

                <div className="search">
                    <form className="d-flex" onSubmit={submitSearch}>
                        <input className="form-control me-2" name="searchText" type="search" placeholder="책 제목 검색" aria-label="Search"/>
                        <button className="btn btn-outline-success" type="submit">Search</button>
                    </form>
                </div>
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">전체 대여/반납 기록</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">활동</th>
                                        <th className="text-align-center">등록일</th>
                                        <th className="text-align-center">회원 이름</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {data.bookLogList.map((log)=>(
                                            <tr>
                                                <td className="text-align-center">{log.bookNumber}</td>
                                                <td>{log.bookName}</td>
                                                <td className="text-align-center">{log.bookStatus}</td>
                                                <td className="text-align-center">{log.regDate}</td>
                                                <td className="text-align-center">{log.userName}</td>
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

export default AdminBookLog;
