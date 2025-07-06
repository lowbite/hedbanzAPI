package com.hedbanz.hedbanzAPI;

/*
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)*/
public class PlayerTest {
    /*@LocalServerPort
    int localServerPort;
    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private MessageService messageService;
    @Autowired
    private RoomService roomService;

    @Test
    public void checkVoting() {
        Thread task1 = new Thread(() -> {
            QuestionDto questionDto1;
            QuestionDto questionDto2;
            for (int j = 0; j < 10; j++) {
                Question question = messageService.addSettingQuestionMessage(309L, 13L);
                messageService.addQuestionText(question.getId(), "asdasd");
                questionDto1 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(9L).build())
                        .setRoomId(309L)
                        .setVoteType(2)
                        .build();
                questionDto2 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(14L).build())
                        .setRoomId(309L)
                        .setVoteType(2)
                        .build();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
                HttpEntity entity1 = new HttpEntity<>(questionDto1, headers);
                HttpEntity entity2 = new HttpEntity<>(questionDto2, headers);
                Thread thread1 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity1, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread thread2 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity2, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                thread1.start();
                thread2.start();

                try {
                    thread1.join();
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread task2 = new Thread(() -> {
            QuestionDto questionDto1;
            QuestionDto questionDto2;
            for (int j = 0; j < 10; j++) {
                Question question = messageService.addSettingQuestionMessage(309L, 13L);
                messageService.addQuestionText(question.getId(), "asdasd");
                questionDto1 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(9L).build())
                        .setRoomId(304L)
                        .setVoteType(2)
                        .build();
                questionDto2 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(14L).build())
                        .setRoomId(304L)
                        .setVoteType(2)
                        .build();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
                HttpEntity entity1 = new HttpEntity<>(questionDto1, headers);
                HttpEntity entity2 = new HttpEntity<>(questionDto2, headers);
                Thread thread1 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity1, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread thread2 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity2, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                thread1.start();
                thread2.start();

                try {
                    thread1.join();
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread task3 = new Thread(() -> {
            QuestionDto questionDto1;
            QuestionDto questionDto2;
            for (int j = 0; j < 10; j++) {
                Question question = messageService.addSettingQuestionMessage(309L, 13L);
                messageService.addQuestionText(question.getId(), "asdasd");
                questionDto1 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(9L).build())
                        .setRoomId(302L)
                        .setVoteType(2)
                        .build();
                questionDto2 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(14L).build())
                        .setRoomId(302L)
                        .setVoteType(2)
                        .build();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
                HttpEntity entity1 = new HttpEntity<>(questionDto1, headers);
                HttpEntity entity2 = new HttpEntity<>(questionDto2, headers);
                Thread thread1 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity1, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread thread2 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity2, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                thread1.start();
                thread2.start();

                try {
                    thread1.join();
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread task4 = new Thread(() -> {
            QuestionDto questionDto1;
            QuestionDto questionDto2;
            for (int j = 0; j < 10; j++) {
                Question question = messageService.addSettingQuestionMessage(309L, 13L);
                messageService.addQuestionText(question.getId(), "asdasd");
                questionDto1 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(9L).build())
                        .setRoomId(247L)
                        .setVoteType(2)
                        .build();
                questionDto2 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(14L).build())
                        .setRoomId(247L)
                        .setVoteType(2)
                        .build();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
                HttpEntity entity1 = new HttpEntity<>(questionDto1, headers);
                HttpEntity entity2 = new HttpEntity<>(questionDto2, headers);
                Thread thread1 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity1, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread thread2 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity2, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                thread1.start();
                thread2.start();

                try {
                    thread1.join();
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread task5 = new Thread(() -> {
            QuestionDto questionDto1;
            QuestionDto questionDto2;
            for (int j = 0; j < 10; j++) {
                Question question = messageService.addSettingQuestionMessage(309L, 13L);
                messageService.addQuestionText(question.getId(), "asdasd");
                questionDto1 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(9L).build())
                        .setRoomId(208L)
                        .setVoteType(2)
                        .build();
                questionDto2 = new QuestionDto.QuestionDTOBuilder()
                        .setQuestionId(question.getId())
                        .setSenderUser(new UserDto.Builder().setId(14L).build())
                        .setRoomId(208L)
                        .setVoteType(2)
                        .build();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
                HttpEntity entity1 = new HttpEntity<>(questionDto1, headers);
                HttpEntity entity2 = new HttpEntity<>(questionDto2, headers);
                Thread thread1 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity1, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread thread2 = new Thread(() -> {
                    restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity2, String.class);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                thread1.start();
                thread2.start();

                try {
                    thread1.join();
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        task1.start();
        task2.start();
        task3.start();
        task4.start();
        task5.start();

        try {
            task1.join();
            task2.join();
            task3.join();
            task4.join();
            task5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addSameVote() {
        QuestionDto questionDto1;
        questionDto1 = new QuestionDto.QuestionDTOBuilder()
                .setQuestionId(994L)
                .setSenderUser(new UserDto.Builder().setId(9L).build())
                .setRoomId(208L)
                .setVoteType(2)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
        HttpEntity entity1 = new HttpEntity<>(questionDto1, headers);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                restTemplate.postForObject("http://localhost:" + 8085 + "/game/player/add-vote", entity1, String.class);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUsersToRoom() {
        Room room = new Room();
        room.setName("4");
        room.setStickerId(1L);
        room.setIconId(1L);
        room.setMaxPlayers(8);
        room = roomService.addRoom(room, 9L);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
        HttpEntity entity = new HttpEntity<>(
                new UserToRoomDto.Builder()
                        .setRoomId(room.getId())
                        .setUserId(14L)
                        .build(),
                headers
        );
        for (int i = 0; i < 7; i++) {
            new Thread(() -> {
                restTemplate.postForObject("http://localhost:" + 8085 + "/rooms/join-user", entity, String.class);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getNextGuessingPlayer() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiaWF0IjoxNTM2NjYxNzEwLCJleHAiOjE1NTIyMTM3MTB9.at-vcgyFptxXF5-4i9cN53qgUn-KpQZIjurNFFu5sEjXkK4Poq1M3SiHbosG3QXb65Nbl-mxFiCxCjglHHxXrQ"));
        HttpEntity entity = new HttpEntity<>(
                new QuestionDto.QuestionDTOBuilder().setAttempt(2).build(),
                headers
        );
        for (int i = 0; i < 7; i++) {
            new Thread(() -> {
                ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + 8085 + "/game/room/318/next-player", HttpMethod.POST, entity, String.class);
                System.out.println(response.getBody());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
}


