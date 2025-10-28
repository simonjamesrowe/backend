package com.simonjamesrowe.apigateway.test.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.SendGrid;
import com.simonjamesrowe.apigateway.core.usecase.IResumeUseCase;
import com.simonjamesrowe.component.test.BaseComponentTest;
import com.simonjamesrowe.component.test.ComponentTest;
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer;
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@ComponentTest
@WithKafkaContainer
@ActiveProfiles("webhookControllerTest")
class WebhookControllerTest extends BaseComponentTest {

    @Autowired
    private TestStreamListener testStreamListener;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SendGrid sendGrid;

    @MockBean
    private IResumeUseCase resumeUseCase;

    @BeforeEach
    void beforeEach() {
        testStreamListener.events.clear();
    }

    @Test
    void webhookHandlerShouldBeSecured() {
        given()
            .log()
            .all()
            .contentType("application/json")
            .post("/webhook")
            .then()
            .log()
            .all()
            .statusCode(401);
    }

    @Test
    void webhookHandlerShouldProduceMessageToKafkaTopic() throws Exception {
        given()
            .log()
            .all()
            .auth().basic("webhook", "password")
            .contentType("application/json")
            .body("""
                {
                  "event": "entry.update",
                  "created_at": "2021-01-02T15:49:35.918Z",
                  "model": "blog",
                  "entry": {
                    "published": true,
                    "tags": [
                      {
                        "_id": "5e495da7bc8d7d001ddbd7c5",
                        "name": "Kubernetes",
                        "createdAt": "2020-02-16T15:20:07.909Z",
                        "updatedAt": "2020-02-16T22:04:13.013Z",
                        "__v": 0,
                        "id": "5e495da7bc8d7d001ddbd7c5"
                      },
                      {
                        "_id": "5f02da709d8081001fd38fa4",
                        "name": "Jenkins",
                        "createdAt": "2020-07-06T08:01:52.572Z",
                        "updatedAt": "2020-07-06T08:01:52.572Z",
                        "__v": 0,
                        "id": "5f02da709d8081001fd38fa4"
                      },
                      {
                        "_id": "5f03901b9d8081001fd38fa7",
                        "name": "Strapi",
                        "createdAt": "2020-07-06T20:56:59.818Z",
                        "updatedAt": "2020-07-06T20:56:59.818Z",
                        "__v": 0,
                        "id": "5f03901b9d8081001fd38fa7"
                      },
                      {
                        "_id": "5f0390249d8081001fd38fa8",
                        "name": "TLS",
                        "createdAt": "2020-07-06T20:57:08.354Z",
                        "updatedAt": "2020-07-06T20:57:08.354Z",
                        "__v": 0,
                        "id": "5f0390249d8081001fd38fa8"
                      },
                      {
                        "_id": "5f0390329d8081001fd38fa9",
                        "name": "MongoDB",
                        "createdAt": "2020-07-06T20:57:22.633Z",
                        "updatedAt": "2020-07-06T20:57:22.633Z",
                        "__v": 0,
                        "id": "5f0390329d8081001fd38fa9"
                      },
                      {
                        "_id": "5f0390499d8081001fd38faa",
                        "name": "React",
                        "createdAt": "2020-07-06T20:57:45.054Z",
                        "updatedAt": "2020-07-06T20:57:45.054Z",
                        "__v": 0,
                        "id": "5f0390499d8081001fd38faa"
                      }
                    ],
                    "skills": [
                      {
                        "_id": "5f635b6a5ee4c9001d2b9632",
                        "name": "Spring Boot",
                        "rating": 9.7,
                        "order": 1,
                        "createdAt": "2020-09-17T12:49:46.739Z",
                        "updatedAt": "2020-09-21T21:05:20.308Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f6851a85ee4c9001d2b96af",
                          "name": "spring-boot.jpeg",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "spring_boot_5ee37de4c1",
                          "ext": ".jpeg",
                          "mime": "image/png",
                          "size": 7.98,
                          "width": 400,
                          "height": 400,
                          "url": "/uploads/spring_boot_5ee37de4c1.jpeg",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_spring-boot.jpeg",
                              "hash": "thumbnail_spring_boot_5ee37de4c1",
                              "ext": ".jpeg",
                              "mime": "image/png",
                              "width": 156,
                              "height": 156,
                              "size": 5.21,
                              "path": null,
                              "url": "/uploads/thumbnail_spring_boot_5ee37de4c1.jpeg"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635b6a5ee4c9001d2b9632",
                            "5feed4c667bdd6001e9c12d0"
                          ],
                          "createdAt": "2020-09-21T07:09:28.458Z",
                          "updatedAt": "2021-01-01T21:02:32.991Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f6851a85ee4c9001d2b96af"
                        },
                        "description": "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can \\"just run\\".\\n\\n- Embed Tomcat, Jetty or Undertow directly (no need to deploy WAR files)\\n- Provide opinionated 'starter' dependencies to simplify your build configuration\\n- Automatically configure Spring and 3rd party libraries whenever possible\\n- Provide production-ready features such as metrics, health checks, and externalized configuration",
                        "id": "5f635b6a5ee4c9001d2b9632"
                      },
                      {
                        "_id": "5f635b555ee4c9001d2b9631",
                        "name": "Kotlin",
                        "rating": 8,
                        "createdAt": "2020-09-17T12:49:25.636Z",
                        "updatedAt": "2020-09-21T21:01:54.939Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f65ec765ee4c9001d2b9676",
                          "name": "kotlin_250x250.png",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "kotlin_250x250_dfc086e243",
                          "ext": ".png",
                          "mime": "image/png",
                          "size": 3.69,
                          "width": 250,
                          "height": 250,
                          "url": "/uploads/kotlin_250x250_dfc086e243.png",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_kotlin_250x250.png",
                              "hash": "thumbnail_kotlin_250x250_dfc086e243",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 156,
                              "height": 156,
                              "size": 6.89,
                              "path": null,
                              "url": "/uploads/thumbnail_kotlin_250x250_dfc086e243.png"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f6359c55ee4c9001d2b9627",
                            "5f635b555ee4c9001d2b9631"
                          ],
                          "createdAt": "2020-09-19T11:33:10.896Z",
                          "updatedAt": "2020-09-21T06:17:06.069Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f65ec765ee4c9001d2b9676"
                        },
                        "description": "- Lambda expressions + Inline functions = performant custom control structures\\n- Extension functions\\n- Null-safety\\n- Smart casts\\n- String templates\\n- Properties\\n- Primary constructors\\n- First-class delegation\\n- Type inference for variable and property types\\n- Singletons\\n- Declaration-site variance & Type projections\\n- Range expressions\\n- Operator overloading\\n- Companion objects\\n- Data classes\\n- Separate interfaces for read-only and mutable collections\\n- Coroutines",
                        "id": "5f635b555ee4c9001d2b9631"
                      },
                      {
                        "_id": "5f635c2e5ee4c9001d2b963c",
                        "name": "Jenkins Pipeline",
                        "rating": 8,
                        "createdAt": "2020-09-17T12:53:02.588Z",
                        "updatedAt": "2020-09-22T06:41:07.845Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f65ed105ee4c9001d2b967c",
                          "name": "jenkins-5-569553.png",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "jenkins_5_569553_cb54b85a4c",
                          "ext": ".png",
                          "mime": "image/png",
                          "size": 12.15,
                          "width": 256,
                          "height": 256,
                          "url": "/uploads/jenkins_5_569553_cb54b85a4c.png",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_jenkins-5-569553.png",
                              "hash": "thumbnail_jenkins_5_569553_cb54b85a4c",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 156,
                              "height": 156,
                              "size": 16.21,
                              "path": null,
                              "url": "/uploads/thumbnail_jenkins_5_569553_cb54b85a4c.png"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635a195ee4c9001d2b962a",
                            "5f635c2e5ee4c9001d2b963c"
                          ],
                          "createdAt": "2020-09-19T11:35:44.624Z",
                          "updatedAt": "2020-09-21T07:16:50.216Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f65ed105ee4c9001d2b967c"
                        },
                        "description": "Pipeline is a suite of plugins that supports implementing and integrating continuous delivery pipelines into Jenkins. Pipeline provides an extensible set of tools for modelling simple-to-complex delivery pipelines \\"as code\\" via the Pipeline DSL (groovy). ",
                        "id": "5f635c2e5ee4c9001d2b963c"
                      },
                      {
                        "_id": "5f635c375ee4c9001d2b963d",
                        "name": "Jenkins X",
                        "rating": 9,
                        "createdAt": "2020-09-17T12:53:11.954Z",
                        "updatedAt": "2020-09-22T21:19:16.887Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f6616545ee4c9001d2b9690",
                          "name": "jenkinsx.png",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "jenkinsx_d56c6fea15",
                          "ext": ".png",
                          "mime": "image/png",
                          "size": 25.16,
                          "width": 696,
                          "height": 398,
                          "url": "/uploads/jenkinsx_d56c6fea15.png",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_jenkinsx.png",
                              "hash": "thumbnail_jenkinsx_d56c6fea15",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 245,
                              "height": 140,
                              "size": 9.18,
                              "path": null,
                              "url": "/uploads/thumbnail_jenkinsx_d56c6fea15.png"
                            },
                            "small": {
                              "name": "small_jenkinsx.png",
                              "hash": "small_jenkinsx_d56c6fea15",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 500,
                              "height": 286,
                              "size": 22.51,
                              "path": null,
                              "url": "/uploads/small_jenkinsx_d56c6fea15.png"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f0413739d8081001fd38fb6",
                            "5f635c375ee4c9001d2b963d"
                          ],
                          "createdAt": "2020-09-19T14:31:48.129Z",
                          "updatedAt": "2020-09-21T07:17:01.500Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f6616545ee4c9001d2b9690"
                        },
                        "description": "Jenkins X provides pipeline automation, built-in GitOps, and preview environments to help teams collaborate and accelerate their software delivery at any scale. Jenkins X builds upon the following core components:\\n- Kubernetes and Docker\\n- Helm and Draft",
                        "id": "5f635c375ee4c9001d2b963d"
                      },
                      {
                        "_id": "5f635f8f5ee4c9001d2b966c",
                        "name": "Helm",
                        "rating": 8,
                        "createdAt": "2020-09-17T13:07:27.527Z",
                        "updatedAt": "2020-09-30T06:20:10.213Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f6854785ee4c9001d2b96cf",
                          "name": "helm.jpg",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "helm_9127651c35",
                          "ext": ".jpg",
                          "mime": "image/jpeg",
                          "size": 12.82,
                          "width": 366,
                          "height": 366,
                          "url": "/uploads/helm_9127651c35.jpg",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_helm.jpg",
                              "hash": "thumbnail_helm_9127651c35",
                              "ext": ".jpg",
                              "mime": "image/jpeg",
                              "width": 156,
                              "height": 156,
                              "size": 5.11,
                              "path": null,
                              "url": "/uploads/thumbnail_helm_9127651c35.jpg"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635f8f5ee4c9001d2b966c"
                          ],
                          "createdAt": "2020-09-21T07:21:28.306Z",
                          "updatedAt": "2020-09-21T07:21:30.887Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f6854785ee4c9001d2b96cf"
                        },
                        "description": "Helm is a tool that streamlines installing and managing Kubernetes applications. ... A single chart might be used to deploy something simple, like a memcached pod, or something complex, like a full web app stack with HTTP servers, databases, caches, etc,",
                        "id": "5f635f8f5ee4c9001d2b966c"
                      },
                      {
                        "_id": "5f635f995ee4c9001d2b966d",
                        "name": "Chart Museum",
                        "rating": 7,
                        "createdAt": "2020-09-17T13:07:37.145Z",
                        "updatedAt": "2020-09-30T06:22:03.609Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f6854c05ee4c9001d2b96d1",
                          "name": "chart-museum.png",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "chart_museum_a56509005a",
                          "ext": ".png",
                          "mime": "image/png",
                          "size": 72.28,
                          "width": 500,
                          "height": 225,
                          "url": "/uploads/chart_museum_a56509005a.png",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_chart-museum.png",
                              "hash": "thumbnail_chart_museum_a56509005a",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 245,
                              "height": 110,
                              "size": 29.64,
                              "path": null,
                              "url": "/uploads/thumbnail_chart_museum_a56509005a.png"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635f995ee4c9001d2b966d"
                          ],
                          "createdAt": "2020-09-21T07:22:40.798Z",
                          "updatedAt": "2020-09-21T07:22:43.401Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f6854c05ee4c9001d2b96d1"
                        },
                        "description": "ChartMuseum is an open-source Helm Chart Repository written in Go (Golang), with support for cloud storage backends, including Google Cloud Storage, Amazon S3, Microsoft Azure Blob Storage, Alibaba Cloud OSS Storage and Openstack Object Storage.",
                        "id": "5f635f995ee4c9001d2b966d"
                      },
                      {
                        "_id": "5f635c185ee4c9001d2b963a",
                        "name": "Maven",
                        "rating": 9.6,
                        "createdAt": "2020-09-17T12:52:40.922Z",
                        "updatedAt": "2020-09-22T06:36:50.576Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f6853265ee4c9001d2b96bf",
                          "name": "maven.png",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "maven_8510b1d6fa",
                          "ext": ".png",
                          "mime": "image/png",
                          "size": 30.77,
                          "width": 1280,
                          "height": 324,
                          "url": "/uploads/maven_8510b1d6fa.png",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_maven.png",
                              "hash": "thumbnail_maven_8510b1d6fa",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 245,
                              "height": 62,
                              "size": 10.07,
                              "path": null,
                              "url": "/uploads/thumbnail_maven_8510b1d6fa.png"
                            },
                            "large": {
                              "name": "large_maven.png",
                              "hash": "large_maven_8510b1d6fa",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 1000,
                              "height": 253,
                              "size": 68.02,
                              "path": null,
                              "url": "/uploads/large_maven_8510b1d6fa.png"
                            },
                            "medium": {
                              "name": "medium_maven.png",
                              "hash": "medium_maven_8510b1d6fa",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 750,
                              "height": 190,
                              "size": 46.3,
                              "path": null,
                              "url": "/uploads/medium_maven_8510b1d6fa.png"
                            },
                            "small": {
                              "name": "small_maven.png",
                              "hash": "small_maven_8510b1d6fa",
                              "ext": ".png",
                              "mime": "image/png",
                              "width": 500,
                              "height": 127,
                              "size": 27.01,
                              "path": null,
                              "url": "/uploads/small_maven_8510b1d6fa.png"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635c185ee4c9001d2b963a"
                          ],
                          "createdAt": "2020-09-21T07:15:50.288Z",
                          "updatedAt": "2020-09-21T07:15:57.208Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f6853265ee4c9001d2b96bf"
                        },
                        "description": "Maven is a build automation tool used primarily for Java projects. Maven can also be used to build and manage projects written in C#, Ruby, Scala, and other languages. \\n\\nMaven addresses two aspects of building software: how software is built, and its dependencies. An XML file describes the software project being built, its dependencies on other external modules and components, the build order, directories, and required plug-ins. ",
                        "id": "5f635c185ee4c9001d2b963a"
                      },
                      {
                        "_id": "5f635c4f5ee4c9001d2b963f",
                        "name": "Tekton",
                        "createdAt": "2020-09-17T12:53:35.722Z",
                        "updatedAt": "2020-09-22T21:20:48.795Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "rating": 8,
                        "image": {
                          "_id": "5f6853a55ee4c9001d2b96c7",
                          "name": "tekton.jpeg",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "tekton_8b8bad63e8",
                          "ext": ".jpeg",
                          "mime": "image/jpeg",
                          "size": 13.24,
                          "width": 213,
                          "height": 237,
                          "url": "/uploads/tekton_8b8bad63e8.jpeg",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_tekton.jpeg",
                              "hash": "thumbnail_tekton_8b8bad63e8",
                              "ext": ".jpeg",
                              "mime": "image/jpeg",
                              "width": 140,
                              "height": 156,
                              "size": 7.5,
                              "path": null,
                              "url": "/uploads/thumbnail_tekton_8b8bad63e8.jpeg"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635c4f5ee4c9001d2b963f"
                          ],
                          "createdAt": "2020-09-21T07:17:57.258Z",
                          "updatedAt": "2020-09-21T07:17:59.250Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f6853a55ee4c9001d2b96c7"
                        },
                        "description": "The Tekton Pipelines project provides k8s-style resources for declaring CI/CD-style pipelines.\\n\\nTekton Pipelines are Cloud Native:\\n\\n- Run on Kubernetes\\n- Have Kubernetes clusters as a first class type\\n- Use containers as their building blocks\\n",
                        "id": "5f635c4f5ee4c9001d2b963f"
                      },
                      {
                        "_id": "5f635ca45ee4c9001d2b9643",
                        "name": "Kubernetes",
                        "rating": 8,
                        "createdAt": "2020-09-17T12:55:00.632Z",
                        "updatedAt": "2020-09-22T21:28:44.327Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f66163d5ee4c9001d2b968e",
                          "name": "kubernetes.jpeg",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "kubernetes_c13e33fa8c",
                          "ext": ".jpeg",
                          "mime": "image/jpeg",
                          "size": 17.62,
                          "width": 400,
                          "height": 388,
                          "url": "/uploads/kubernetes_c13e33fa8c.jpeg",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_kubernetes.jpeg",
                              "hash": "thumbnail_kubernetes_c13e33fa8c",
                              "ext": ".jpeg",
                              "mime": "image/jpeg",
                              "width": 161,
                              "height": 156,
                              "size": 6.17,
                              "path": null,
                              "url": "/uploads/thumbnail_kubernetes_c13e33fa8c.jpeg"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f0408c59d8081001fd38fb1",
                            "5f635ca45ee4c9001d2b9643"
                          ],
                          "createdAt": "2020-09-19T14:31:25.284Z",
                          "updatedAt": "2020-09-21T12:08:28.703Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f66163d5ee4c9001d2b968e"
                        },
                        "description": "Kubernetes is a portable, extensible, open-source platform for managing containerized workloads and services, that facilitates both declarative configuration and automation. It has a large, rapidly growing ecosystem. Kubernetes provides you with:\\n\\n- **Service discovery and load balancing**\\n- **Automated rollouts and rollbacks**: You provide Kubernetes with a cluster of nodes that it can use to run containerized tasks. You tell Kubernetes how much CPU and memory (RAM) each container needs. Kubernetes can fit containers onto your nodes to make the best use of your resources.\\n- **Self-healing** Kubernetes restarts containers that fail, replaces containers, kills containers that don't respond to your user-defined health check, and doesn't advertise them to clients until they are ready to serve.\\n- **Secret and configuration management** ",
                        "id": "5f635ca45ee4c9001d2b9643"
                      },
                      {
                        "_id": "5f635e935ee4c9001d2b9665",
                        "name": "Typescript",
                        "rating": 7.5,
                        "createdAt": "2020-09-17T13:03:15.410Z",
                        "updatedAt": "2020-09-27T08:10:06.054Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f689aee76320d001e515e8b",
                          "name": "ts.jpg",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "ts_1c7c89d06d",
                          "ext": ".jpg",
                          "mime": "image/jpeg",
                          "size": 11.59,
                          "width": 400,
                          "height": 400,
                          "url": "/uploads/ts_1c7c89d06d.jpg",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_ts.jpg",
                              "hash": "thumbnail_ts_1c7c89d06d",
                              "ext": ".jpg",
                              "mime": "image/jpeg",
                              "width": 156,
                              "height": 156,
                              "size": 4.38,
                              "path": null,
                              "url": "/uploads/thumbnail_ts_1c7c89d06d.jpg"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f635e935ee4c9001d2b9665"
                          ],
                          "createdAt": "2020-09-21T12:22:06.356Z",
                          "updatedAt": "2020-09-21T12:22:08.459Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f689aee76320d001e515e8b"
                        },
                        "description": "TypeScript is an open-source programming language developed and maintained by Microsoft. It is a strict syntactical superset of JavaScript and adds optional static typing to the language. TypeScript is designed for development of large applications and transcompiles to JavaScript",
                        "id": "5f635e935ee4c9001d2b9665"
                      },
                      {
                        "_id": "5f635e625ee4c9001d2b9660",
                        "name": "React",
                        "rating": 8.5,
                        "createdAt": "2020-09-17T13:02:26.277Z",
                        "updatedAt": "2020-09-27T08:07:00.023Z",
                        "__v": 0,
                        "created_by": "5e4839cd1c2d53001dcc0696",
                        "updated_by": "5e4839cd1c2d53001dcc0696",
                        "image": {
                          "_id": "5f66167a5ee4c9001d2b9692",
                          "name": "react.jpeg",
                          "alternativeText": "",
                          "caption": "",
                          "hash": "react_56f28418d3",
                          "ext": ".jpeg",
                          "mime": "image/jpeg",
                          "size": 11.98,
                          "width": 500,
                          "height": 353,
                          "url": "/uploads/react_56f28418d3.jpeg",
                          "formats": {
                            "thumbnail": {
                              "name": "thumbnail_react.jpeg",
                              "hash": "thumbnail_react_56f28418d3",
                              "ext": ".jpeg",
                              "mime": "image/jpeg",
                              "width": 221,
                              "height": 156,
                              "size": 4.08,
                              "path": null,
                              "url": "/uploads/thumbnail_react_56f28418d3.jpeg"
                            }
                          },
                          "provider": "local",
                          "related": [
                            "5f04152d9d8081001fd38fb9",
                            "5f635e625ee4c9001d2b9660"
                          ],
                          "createdAt": "2020-09-19T14:32:26.690Z",
                          "updatedAt": "2020-09-21T12:19:36.732Z",
                          "__v": 0,
                          "created_by": "5e4839cd1c2d53001dcc0696",
                          "updated_by": "5e4839cd1c2d53001dcc0696",
                          "id": "5f66167a5ee4c9001d2b9692"
                        },
                        "description": "React.js is an open-source JavaScript library that is used for building user interfaces specifically for single-page applications. It's used for handling the view layer for web and mobile apps.\\n\\nReact allows developers to create large web applications that can change data, without reloading the page. The main purpose of React is to be fast, scalable, and simple. It works only on user interfaces in the application",
                        "id": "5f635e625ee4c9001d2b9660"
                      }
                    ],
                    "_id": "5f0215c69d8081001fd38fa1",
                    "title": "Creating a rich web app that can be hosted from home",
                    "shortDescription": "A quick introduction into the various components of my personal website including jenkinsx, kubernetes, letsencrypt and more.",
                    "createdAt": "2020-07-05T18:02:46.731Z",
                    "updatedAt": "2021-01-02T15:49:35.901Z",
                    "__v": 0,
                    "image": {
                      "_id": "5f6616045ee4c9001d2b968a",
                      "name": "Website Icon.jpeg",
                      "alternativeText": "",
                      "caption": "",
                      "hash": "Website_Icon_727b161f01",
                      "ext": ".jpeg",
                      "mime": "image/jpeg",
                      "size": 29.24,
                      "width": 801,
                      "height": 401,
                      "url": "/uploads/Website_Icon_727b161f01.jpeg",
                      "formats": {
                        "thumbnail": {
                          "name": "thumbnail_Website Icon.jpeg",
                          "hash": "thumbnail_Website_Icon_727b161f01",
                          "ext": ".jpeg",
                          "mime": "image/jpeg",
                          "width": 245,
                          "height": 123,
                          "size": 6.52,
                          "path": null,
                          "url": "/uploads/thumbnail_Website_Icon_727b161f01.jpeg"
                        },
                        "medium": {
                          "name": "medium_Website Icon.jpeg",
                          "hash": "medium_Website_Icon_727b161f01",
                          "ext": ".jpeg",
                          "mime": "image/jpeg",
                          "width": 750,
                          "height": 375,
                          "size": 27.25,
                          "path": null,
                          "url": "/uploads/medium_Website_Icon_727b161f01.jpeg"
                        },
                        "small": {
                          "name": "small_Website Icon.jpeg",
                          "hash": "small_Website_Icon_727b161f01",
                          "ext": ".jpeg",
                          "mime": "image/jpeg",
                          "width": 500,
                          "height": 250,
                          "size": 16.14,
                          "path": null,
                          "url": "/uploads/small_Website_Icon_727b161f01.jpeg"
                        }
                      },
                      "provider": "local",
                      "related": [
                        "5f0215c69d8081001fd38fa1"
                      ],
                      "createdAt": "2020-09-19T14:30:28.441Z",
                      "updatedAt": "2020-09-19T14:30:33.163Z",
                      "__v": 0,
                      "created_by": "5e4839cd1c2d53001dcc0696",
                      "updated_by": "5e4839cd1c2d53001dcc0696",
                      "id": "5f6616045ee4c9001d2b968a"
                    },
                    "content": "I'm the kind of person that learns by doing - the more I do, the more it sinks in. Whilst attempting to keep my skills up to date, I had the idea of building a rich web application which would serve up my CV, and it could also double as a place to hone and demonstrate my skillsets in certain technologies. Eventually, I could even start blogging about these experiences. \\n\\nThe first thing I decided I would need would be a CMS (Content Management System). The content of my website would have to be dynamic, as I would like to keep my CV and blog up to date.  There were many options available here, but I finally settled on a headless CMS named ([strapi](https://strapi.io/)). Strapi would also require some sort of persistence layer, and  [mongodb](https://www.mongodb.com/ seemed to be a sensible choice.\\n\\nFor the web interface itself, there was a bit of competition between React and Angular. After spending years commercially working on Angular projects, I decided that React could be a good thing to learn and went with that.  \\n\\nnpx create-react-app my-app --template typescript is the command used to boostrap my react SPA (single page application) and I found a boostrap template from [here](https://wrapbootstrap.com/).\\n\\nSo far so good right ? I had picked the basic building blocks for building this dynamic CV/blog, but where would I run it ? I had a perfectly good iMac that was unused, and a fast enough internet connection. This app doesnt need to be highly available, as its just a little hobby app so this would suit well. I could use [ngrok](https://ngrok.com/) to make my app available to the internet, and I could use [Route 53](https://aws.amazon.com/route53/) to set up and configure my domain.\\n\\nKubernetes is a very popular way or running cloud native apps, so this was a pretty easy choice, the only issue was what flavour of it would work best on a mac. I started off with minikube, but started to have issues with persistent volumes, so eventually went with [kind (kubernetes in docker)](https://kind.sigs.k8s.io/). I could easily install nginx ingress controller, and it was super easy to configure.\\n\\nThe last thing to consider was how to deploy the individual components or services to the kubernetes cluster. One of my requirements was that I would want to make continual improvments to my app, so the deployment process must be as simple as possible. I would want to have git projects for each service,  build a container image, and deploy a helm chart (a helm chart is a collection of files that describe a related set of Kubernetes resources).  [Jenkins X](https://jenkins-x.io/) describes itself as *Jenkins X provides pipeline automation, built-in GitOps, and preview environments to help teams collaborate and accelerate their software delivery at any scale*. There was out of the box support for kubernetes flavours such as GKE (google cloud) and EKS (amazon web services), but a bit of customisation would be required if i wanted to run this on my local kubernetes cluster. This was easy enough and I was able to configure the following:\\n- [Harbor](https://goharbor.io/) as the container registry of choice\\n- [Cert Manager](https://cert-manager.io/) - configure cert manager to generate lets encrpt certificates using ACME challennges based on my use of AWS Route 53 for dns.\\n- Installed MongoDB in the control plane.\\n\\nOverall my initial architecture looked something like this:\\n\\n![Website V1](/uploads/Website_V1_c3889a1afa.jpeg)\\n\\n\\n*All source code for this app can be found here: (https://github.com/simonjamesrowe)*",
                    "updated_by": {
                      "isActive": true,
                      "blocked": false,
                      "roles": [
                        "5f6314095ee4c9001d2b959f"
                      ],
                      "_id": "5e4839cd1c2d53001dcc0696",
                      "username": "simon.rowe@gmail.com",
                      "email": "simon.rowe@gmail.com",
                      "createdAt": "2020-02-15T18:34:53.155Z",
                      "updatedAt": "2020-09-17T07:45:13.526Z",
                      "__v": 0,
                      "id": "5e4839cd1c2d53001dcc0696"
                    },
                    "id": "5f0215c69d8081001fd38fa1"
                  }
                }
                """)
            .post("/webhook")
            .then()
            .log()
            .all()
            .statusCode(200);

        await().atMost(Duration.ofSeconds(30)).until(() -> !testStreamListener.events.isEmpty());

        Message<List<WebhookEventDTO>> blogEventMessage = testStreamListener.events.get(0);
        BlogResponseDTO blog = objectMapper.convertValue(
                blogEventMessage.getPayload().get(0).entry(),
                BlogResponseDTO.class
        );
        String key = (String) blogEventMessage.getHeaders().get("kafka_receivedMessageKey");
        String modelType = (String) blogEventMessage.getHeaders().get("model");
        Long traceId = (Long) blogEventMessage.getHeaders().get("b3");

        assertThat(traceId).isNotNull();
        assertThat(modelType).isEqualTo("blog");
        assertThat(key).isEqualTo("blog-5f0215c69d8081001fd38fa1");
        assertThat(blog.title()).isEqualTo("Creating a rich web app that can be hosted from home");
        assertThat(blog.tags()).hasSize(6);
        assertThat(blog.published()).isTrue();
        assertThat(blog.skills()).hasSize(11);
        assertThat(blog.createdAt()).isEqualTo(ZonedDateTime.parse("2020-07-05T18:02:46.731Z"));
        assertThat(blog.updatedAt()).isEqualTo(ZonedDateTime.parse("2021-01-02T15:49:35.901Z"));
        assertThat(blog.image().url()).isEqualTo("/uploads/Website_Icon_727b161f01.jpeg");
        assertThat(blog.image().name()).isEqualTo("Website Icon.jpeg");
        assertThat(blog.image().size()).isEqualTo(29);
        assertThat(blog.image().width()).isEqualTo(801);
        assertThat(blog.image().height()).isEqualTo(401);
        assertThat(blog.image().mime()).isEqualTo("image/jpeg");
        assertThat(blog.image().formats().thumbnail()).isNotNull();
        assertThat(blog.image().formats().small()).isNotNull();
        assertThat(blog.image().formats().medium()).isNotNull();
        assertThat(blog.image().formats().large()).isNull();

        verify(resumeUseCase).regenerateResume();
    }
}

@Configuration
@Profile("webhookControllerTest")
class TestStreamListener {

    public final CopyOnWriteArrayList<Message<List<WebhookEventDTO>>> events = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "${namespace:LOCAL}_EVENTS")
    public void consume(Message<List<WebhookEventDTO>> it) {
        events.add(it);
    }
}