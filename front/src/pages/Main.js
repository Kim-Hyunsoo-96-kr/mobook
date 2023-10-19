import '../App.css';
import Header from "../comp/Header";
import Section1 from "../comp/Section1";
import Section2 from "../comp/Section2";
import DashboardSection1 from "../comp/DashboardSection1";
import DashboardSection2 from "../comp/DashboardSection2";

function Main() {
  return (
          <div>
            <Header/>
            <DashboardSection1/>
            <Section1/>
            <Section2/>
          </div>
  );
}

export default Main;
