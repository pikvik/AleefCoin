import React from 'react';

export default class AboutUs extends React.Component {
    render() {
        return (
            <div>
                <section id="aboutUs">
                    <div className="container">
                        <h2 className="upperCase abouth2">About Us</h2>
                        <h4 className="upperCase abouth2 lineHeight">ALEEF Group of Companies</h4>
                        <div className="col-md-12">
                            <div className="col-md-6 col-sm-6 text-left">
                                <p>Aleef Group of companies is a 2 decades old group of companies the group has footprints in Dubai & India into several lucrative businesses with a vision of growth of society and catering to basic needs at an affordable price. The group is presently in multiple businesses mainly import & export, Healthcare, Real estate, organic foods and education. The group believes in where Honesty is the only policy. The group donates to the social cause every month to a registered trust.</p>
                                <p>Under Import export division Aleef group is exporting commodities and pulses to many different countries like Bangladesh, Srilanka, Maldives Islands, and UAE. </p>
                                <div className="collapse" id="viewdetails">
                                    <p>Under Real Estate division Aleef Group is providing for all asset classes of Indian real estate like housing, commercial – office space and retail and hospitality. In recent years, the growth has spread out to Tier-II and III cities as well. Aleef Group is having high growth in services as well as the manufacturing sector has resulted in high demand for commercial and industrial real estate. Further, the economic growth has trickled down to the large Indian middle className increasing affordability and affluence. Improving living standards are driving the demand for better quality housing and urban infrastructure.</p>
                                    <p>Under organic food division: Aleef group also has Certified Organic food division to supply the complete range of Organic food products like Grocery, spices, cereals, legumes, edible oils, seeds, and superfoods which are exported to over 25 countries since 2010. The company does private labeling also for over 15 companies and products are sold both offline & online stores.</p>
                                    <p>Under Education division, Aleef Group has educational schools for the less privileged up to K 12 both for girls and boys at a very nominal cost. The group plans to expand its services to Graduation and post-graduation also very soon.</p>
                                    <p>Under Software division Aleef group has a team of very dedicated engineers to cater to most domains like ERP, CRM, WEB-BASED TECHNOLOGIES, APP DEVELOPMENT and recently in Blockchain. The group has an in-house facility for customized Enterprise solutions, integration, and AMC on a global basis.</p>
                                </div>
                                <button type="button" data-toggle="collapse" data-target="#viewdetails" className="bannerBtn">Read More</button>
                            </div>
                            <div className="col-md-6 col-sm-6">
                                <img src="src/public/img/about.png" className="aboutImg" />
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        )
    }
}