import '../App.css';
import {useRecoilValue, useSetRecoilState} from "recoil";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector, queryClient, Toast, Toast2,
} from "../recoil";
import {Link, Navigate, useLocation, useNavigate} from "react-router-dom";
import {useQuery, useQueryClient} from "react-query";
import Pagination from "react-js-pagination";
import Swal from "sweetalert2";
import {useState} from "react";
import CompComment from "../comp/CompComment";
import CompCommentModal from "../comp/CompCommentModal";

const Search = () => {
    const [comment, setComment] = useState('');
    const handleInputChange = (event) => {
        setComment(event.target.value);
    };
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
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
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
            Toast2.fire({
                icon: 'info',
                title: '작업 중...'
            });
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
            else if(e.response.status == 500){
                Swal.fire(
                    '웹훅 오류',
                    '대여 목록을 확인해주세요.<br> 송주환 사원에게 문의해주세요.',
                    'warning'
                )
            }
            else
                Swal.fire(
                    '예상치 못한 오류',
                    error.message,
                    'warning'
                )
        }
    }
    const deleteBook = (bookNumber) => {
        Swal.fire({
            title: '책을 삭제하시겠습니까?',
            text: "삭제한 책은 다시 복구할 수 있습니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '삭제',
            cancelButtonText: '취소',
            reverseButtons: true, // 버튼 순서 거꾸로
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await axiosInstance.post(`${CONFIG.API_BOOK_DELETE}${bookNumber}`);
                    Toast.fire({
                        icon: 'success',
                        title: response.data.message
                    })
                    queryClient.invalidateQueries(["bookList", page, searchText]);

                } catch (e) {
                    if (e.response.status == 400 || e.response.status == 412)
                        Swal.fire(
                            e.response.data.message,
                            '한번 더 확인해주세요.',
                            'warning'
                        )
                    else
                        Swal.fire(
                            '예상치 못한 오류',
                            e.message,
                            'warning'
                        )
                }
            }
        })
    }
    const heartBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RECOMMEND}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '내 책 관리에서 찜한 책 내역을 볼 수 있습니다.',
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
    const addComment = async (event, bookNumber) => {

        event.preventDefault();

        const form = event.target;

        form.comment.value = form.comment.value.trim();

        if (form.comment.value.length === 0) {
            Swal.fire(
                '댓글을 입력해주세요.',
                '빈 값 입니다.',
                'warning'
            )
            form.comment.focus();
            return;
        }
        const comment = form.comment.value;

        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_COMMENT}${bookNumber}`, {comment});
            Toast.fire({
                icon: 'success',
                title: response.data.message
            })
            setComment('');
            queryClient.invalidateQueries(["bookList", page, searchText]);
        } catch (e) {
            console.log(e)
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
                        <div className="input-group">
                            <label className="input-group-text" htmlFor="inputGroupSelect01">Option</label>
                            <select className="form-select me-2" id="inputGroupSelect01">
                                <option value="1">제목</option>
                                <option value="2">책 번호</option>
                                <option value="3">저자</option>
                                <option value="4">출판사</option>
                                <option value="5">설명</option>
                                <option value="6">All</option>
                            </select>
                        </div>
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
                                                        {book.isDeleted &&
                                                            <p>****삭제된 책 입니다****</p>
                                                        }
                                                        <p>번호: {book.bookNumber}</p>
                                                        <p>제목: {book.bookName}</p>
                                                        <p>저자: {book.bookAuthor ? book.bookAuthor.replaceAll("^", ", ") : '저자 정보 없음'}</p>
                                                        <p>출판사: {book.bookPublisher}</p>
                                                        <p>입고일: {book.regDate}</p>
                                                        <p>대여 가능 여부: {book.isAble ? "가능" : "불가능"}</p>
                                                        <p>찜 수: {book.recommend}</p>
                                                        <div style={{display : 'flex', alignItems : 'center'}}>
                                                            <p><button className="btn btn-outline-success btn-sm" onClick={() => heartBook(book.bookNumber)}>
                                                                찜하기
                                                            </button></p>
                                                            {book.isAble &&
                                                            <p style={{marginLeft : '8px'}}><button className="btn btn-outline-primary btn-sm" onClick={() => rentBook(book.bookNumber)}>
                                                                대여하기
                                                            </button></p>
                                                            }
                                                            <CompCommentModal book={book} key={book.id} />
                                                            {isAdmin &&
                                                                <p style={{marginLeft : '8px'}}><button className="btn btn-outline-danger btn-sm" onClick={() => deleteBook(book.bookNumber)}>
                                                                    책 삭제
                                                                </button></p>
                                                            }
                                                        </div>
                                                        <p><a href={book.bookLink} target="_blank" rel="noopener noreferrer">자세히 보기</a></p>
                                                    </div>
                                                </td>
                                                <td class="col-md-5" style={{lineHeight: "25px"}}>
                                                    {book.bookDescription
                                                        ? (book.bookDescription.trim().length > 500
                                                                ? `${book.bookDescription.slice(0, 300)}...`
                                                                : book.bookDescription
                                                        )
                                                        : '설명이 없습니다.'
                                                    }
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
