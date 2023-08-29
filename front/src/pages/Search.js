import '../App.css';
import {useRecoilValue, useSetRecoilState} from "recoil";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector, queryClient, Toast,
} from "../recoil";
import {Link, Navigate, useLocation, useNavigate} from "react-router-dom";
import {useQuery, useQueryClient} from "react-query";
import Pagination from "react-js-pagination";
import Swal from "sweetalert2";

const Search = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["bookList", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_BOOK_SEARCH}?page=${page}&searchText=${searchText}`);
        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
    const handlePageChange = (page) => {
        navigate(`/search?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/search?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/search?searchText=${searchText}&page=1`);
        }
    };
    const rentBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RENT}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '내 책 관리에서 대여 내역을 볼 수 있습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries(["bookList", page, searchText]);
            })
        } catch (e) {
            if(e.response.status == 400)
                Swal.fire(
                e.response.data.message,
                '한번 더 확인해주세요.',
                'warning'
                )
            else
                Swal.fire(
                    '예상치 못한 오류',
                    error.message,
                    'warning'
                )
        }
    }
    const heartBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RECOMMEND}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '내 책 관리에서 추천한 책 내역을 볼 수 있습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries(["bookList", page, searchText]);
            })
        } catch (e) {
            if(e.response.status == 400)
                Swal.fire(
                    e.response.data.message,
                    '한번 더 확인해주세요.',
                    'warning'
                )
            else
                Swal.fire(
                    '예상치 못한 오류',
                    error.message,
                    'warning'
                )
        }
    }

    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }

    return (
        <section className="py-5">
            <div className="container px-5 my-5">
                <div className="text-center mb-5">
                    <h1 className="fw-bolder">책 목록</h1>
                </div>
                <div className="search">
                    <form className="d-flex" onSubmit={submitSearch}>
                        <input className="form-control me-2" name="searchText" type="search" placeholder="책 제목 검색" aria-label="Search"/>
                            <button className="btn btn-outline-success" type="submit">Search</button>
                    </form>
                </div>
                    <div className="container-fluid px-4">
                        <div className="card mb-4 fs-7">
                            <div className="card-body">
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">책 정보</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {data.bookList.map((book)=>(
                                            <tr key={book.bookId}>
                                                <td className="text-align-center">
                                                    <img src={book.bookImageUrl} alt="Book Cover" style={{maxWidth : '180px'}} />
                                                </td>
                                                <td>
                                                    <div>
                                                        <p>번호: {book.bookNumber}</p>
                                                        <p>제목: {book.bookName}</p>
                                                        <p>입고일: {book.regDate}</p>
                                                        <p>추천수: {book.recommend}</p>
                                                        <div style={{display : 'flex', alignItems : 'center'}}>
                                                            <p><button className="btn btn-outline-success btn-sm" onClick={() => heartBook(book.bookNumber)}>
                                                                추천하기
                                                            </button></p>
                                                            <p style={{marginLeft : '8px'}}><button className="btn btn-outline-primary btn-sm" onClick={() => rentBook(book.bookNumber)}>
                                                                대여가능
                                                            </button></p>
                                                        </div>
                                                        <p><a href={book.bookLink} target="_blank" rel="noopener noreferrer">자세히 보기</a></p>
                                                    </div>
                                                </td>
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
        </section>
    );
}

export default Search;
