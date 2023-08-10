import {Link} from "react-router-dom";

function Header() {
    return (
        <header class="bg-dark">
            <div class="container px-5">
                <div class="row gx-5 align-items-center justify-content-center">
                    <div class="col-lg-8 col-xl-7 col-xxl-6">
                        <div class="my-5 text-center text-xl-start">
                            <h1 class="display-6 fw-bolder text-white mb-2">9월 추천 도서 : 아토믹 코틀린</h1>
                            <p class="lead fw-normal text-white-50 mb-4 mt-5">추천 도서는 매월 1일 바뀝니다.<br/>추천 수, 대여 수 등을 기준으로 관리자가 선정합니다.</p>
                            <div class="d-grid gap-3 d-sm-flex justify-content-sm-center justify-content-xl-start">
                                <Link to="/search" class="btn btn-primary btn-lg px-4 me-sm-3 mt-5" href="#features">다른 책 보러가기</Link>
                                <Link to="/login" class="btn btn-outline-light btn-lg px-4 mt-5" href="#!">로그인</Link>
                            </div>
                        </div>
                    </div>
                    <div class="col-xl-5 col-xxl-6 d-none d-xl-block text-center"><img class="img-fluid rounded-3 my-5 height450" src="https://image.yes24.com/goods/117817486/XL" alt="..." /></div>
                </div>
            </div>
        </header>
    );
}

export default Header;
