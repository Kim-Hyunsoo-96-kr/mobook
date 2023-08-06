import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate} from "react-router-dom";
import {useState} from "react";
import {useQuery} from "react-query";
import Pagination from 'react-js-pagination'
function Test() {
    const [page, setPage] = useState(1);
    const handlePageChange = (page) => {
        setPage(page);
        console.log(page)
    };
    const { isLoading, error, data } = useQuery("bookList", async () => {
        // productList 는 캐싱키, 참고로 지금은 캐시 사용 안함
        // 참고로 axiosInstance 로 요청하면 알아서 엑세스 키가 헤더에 붙어서 요청됩니다.
        // 그것은 액시오스 인터셉터에서 자동으로 해줍니다.
        // 우리가 App 함수에서 그렇게 세팅 했습니다.
        const response = await axiosInstance.get(CONFIG.API_BOOK_LIST);

        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }
    if (!isLogined) return <Navigate to="/" replace />; // 로그인 안했다면 메인화면으로 보냄
    return (
        <div>
            <Header/>
            <div className="p">
                <h2>Pagination 연습</h2>
                    <Pagination
                        activePage={page}
                        itemsCountPerPage={10}
                        totalItemsCount={300}
                        pageRangeDisplayed={10}
                        onChange={handlePageChange}>
                    </Pagination>
            </div>
        </div>
    );
}

export default Test;
