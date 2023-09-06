import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector,
    loginedUserInfoAtom,
    loginedUserInfoSelector,
    queryClient
} from "../recoil";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Swal from "sweetalert2";
import Pagination from "react-js-pagination";

const AdminRequestBook = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["myRequestBook", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_ADMIN_REQUESTBOOKLOG}?page=${page}&searchText=${searchText}`);
        return response.data;
    });

    const handlePageChange = (page) => {
        navigate(`/adminRequestBook?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/adminRequestBook?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/adminRequestBook?searchText=${searchText}&page=1`);
        }
    };
    const completeRequest = async (bookRequestId) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_ADMIN_REQUESTBOOK_COMPELTE}${bookRequestId}`);
            Swal.fire(
                response.data.message,
                '신청한 사원에게도 알려주면 기뻐할겁니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries("myRequestBook");
            })
        } catch (e) {
            if(e.response.status == 400 || e.response.status == 412 || e.response.status == 500){
                Swal.fire(
                    e.response.data.message,
                    '관리자에게 문의해주세요.',
                    'warning'
                )
            }
            else
                Swal.fire(
                    '예상치 못한 오류',
                    '이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                    'warning'
                )
        }
    }
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
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
                    <h1 class="fw-bolder">요청한 책 내역</h1>
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
                                    <span class="text-muted fs-4">요청한 책</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">요청일</th>
                                        <th className="text-align-center">처리 완료일</th>
                                        <th className="text-align-center">상태</th>
                                        <th className="text-align-center">회원 이름</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {data.requestBookLogList.map((book, index)=>(
                                        <tr key={index}>
                                            <td className="text-align-center">{index + 1}</td>
                                            <td>{book.bookName}</td>
                                            <td className="text-align-center">{book.requestDate}</td>
                                            <td className="text-align-center">{book.completeDate}</td>
                                            <td className="text-align-center">{book.status}</td>
                                            <td className="text-align-center">{book.userName}</td>
                                            {book.completeDate == "0" ? (<td><button className="btn btn-outline-primary btn-sm" onClick={() => completeRequest(index + 1)}>완료 처리</button></td>) : (<td></td>)}
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

export default AdminRequestBook;
