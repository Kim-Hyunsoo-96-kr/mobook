import {Link} from "react-router-dom";

function Section1() {
    return (
        <section className="py-5">
            <div className="container px-5 my-5">
                <div className="row gx-5 row-cols-1 row-cols-sm-2 row-cols-xl-4 justify-content-center">
                    <div className="col mb-5 mb-5 mb-xl-0">
                        <div className="text-center">
                            <Link to="/search"><img className="img-fluid mb-4 px-4 cursor" src="img/bookMan.png" alt="..." /></Link>
                            <h5 className="fw-bolder">책 대여</h5>
                        </div>
                    </div>
                    <div className="col mb-5 mb-5 mb-xl-0">
                        <div className="text-center">
                            <Link to="/myRentBook"><img className="img-fluid mb-4 px-4 cursor" src="img/bookClock.png" alt="..."/></Link>
                            <h5 className="fw-bolder">책 반납</h5>
                        </div>
                    </div>
                    <div className="col mb-5 mb-5 mb-sm-0">
                        <div className="text-center">
                            <Link to="/search"><img className="img-fluid mb-4 px-4 cursor" src="img/bookList.png" alt="..."/></Link>
                            <h5 className="fw-bolder">책 검색</h5>
                        </div>
                    </div>
                    <div className="col mb-5">
                        <div className="text-center">
                            <Link to="/request"><img className="img-fluid mb-4 px-4 cursor" src="img/bookReq.png" alt="..."/></Link>
                            <h5 className="fw-bolder">책 요청</h5>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default Section1;
