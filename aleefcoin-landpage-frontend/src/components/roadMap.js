import React from 'react';

export default class RoadMap extends React.Component {
    render() {
        return (
            <div>
                <section id="roadmap">
                    <div className="roadmap" >
                        <div className="container">
                            <div className="row">
                                <div className="col-lg-12 text-center rdccc">
                                    <h1 className="page-title animated hiding" data-animation="zoomIn">Road<span>Map</span>
                                    </h1>
                                </div>
                                <div className="col-sm-12 col-md-12 col-lg-12 col-xs-12">
                                    <div className="roadmap">
                                        <img src="src/public/img/roadmap.png" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        )
    }
}