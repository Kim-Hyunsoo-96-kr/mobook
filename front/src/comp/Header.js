import App from "../App";

function Header() {
    return (
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container px-5">
                <a class="navbar-brand" href="index.html">MOBOOK 1.0</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                        <li class="nav-item"><a class="nav-link" href="index.html">홈</a></li>
                        <li class="nav-item"><a class="nav-link" href="about.html">책 검색</a></li>
                        <li class="nav-item"><a class="nav-link" href="contact.html">책 대여</a></li>
                        <li class="nav-item"><a class="nav-link" href="pricing.html">책 반납</a></li>
                        <li class="nav-item"><a class="nav-link" href="faq.html">로그인</a></li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">마이 페이지</a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                <li><a class="dropdown-item" href="blog-home.html">내 정보</a></li>
                                <li><a class="dropdown-item" href="blog-post.html">비밀번호 변경</a></li>
                                <li><a class="dropdown-item" href="blog-post.html">비밀번호 변경</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;
