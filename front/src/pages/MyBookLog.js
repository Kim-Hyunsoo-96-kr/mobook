import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Pagination from "react-js-pagination";

const MyBookLog = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["myBook", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_MYBOOKLOG}?page=${page}&searchText=${searchText}`);
        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const handlePageChange = (page) => {
        navigate(`/myBookLog?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/myBookLog?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/myBookLog?searchText=${searchText}&page=1`);
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
                    <h1 class="fw-bolder">내 기록</h1>
                </div>
                {data.totalCnt > 0 &&
                    <div className="search">
                        <form className="d-flex" onSubmit={submitSearch}>
                            <input className="form-control me-2" name="searchText" type="search" placeholder="책 제목 검색" aria-label="Search"/>
                            <button className="btn btn-outline-success" type="submit">Search</button>
                        </form>
                    </div>
                }
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">내 기록</span>
                                </div>
                                {data.totalCnt > 0 &&
                                <div>
                                    <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">책 정보</th>
                                        <td></td>
                                        <th className="text-align-center">상태</th>
                                        <th className="text-align-center">등록일</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {data.bookLogList.map((log)=>(
                                            <tr>
                                                <td className="text-align-center">
                                                    <img src={log.bookImageUrl} alt="Book Cover" style={{maxWidth : '180px'}} />
                                                </td>
                                                <td>
                                                    <div>
                                                        <p>번호: {log.bookNumber}</p>
                                                        <p>제목: {log.bookName}</p>
                                                        <p><a href={log.bookLink} target="_blank" rel="noopener noreferrer">자세히 보기</a></p>
                                                    </div>
                                                </td>
                                                <td className="text-align-center">{log.bookStatus}</td>
                                                <td className="text-align-center">{log.regDate}</td>
                                                <td></td>
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
                                }
                                {data.totalCnt > 0 ||
                                    <div>
                                        저장된 내 기록이 없습니다.
                                    </div>
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default MyBookLog;
