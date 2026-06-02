package org.sim.umira.resources.UmiraTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.UmiraTest.CreateExamDto;
import org.sim.umira.dtos.UmiraTest.CreateExamResultDto;
import org.sim.umira.dtos.UmiraTest.ResponseExamDto;
import org.sim.umira.dtos.UmiraTest.ResponseExamResultDto;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.UmiraTest.ExamAccessEntity;
import org.sim.umira.entities.UmiraTest.ExamEntity;
import org.sim.umira.entities.UmiraTest.ExamQuestionEntity;
import org.sim.umira.entities.UmiraTest.ExamResultEntity;
import org.sim.umira.entities.UmiraTest.ExamResultQuestionEntity;
import org.sim.umira.entities.UmiraTest.ExamSessionEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.security.UnauthorizedException;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/UmiraTest")
@Secured
public class ExamUmiraRes {

    @Inject
    RedisDataSource redis;

    @GET
    @Path("/get-exam")
    public Response getExam() {
        try {
            // List<ExamEntity> exam = ExamEntity.listAll();
            List<ExamEntity> exam = ExamEntity.find("""
                        SELECT e
                        FROM ExamEntity e
                        JOIN e.examAccess ea
                    """).list();
            List<ResponseExamDto> examResponse = new ArrayList<>();
            for (ExamEntity exE : exam) {
                List<ExamQuestionEntity> exQuest = ExamQuestionEntity.find("exam = ?1", exE).list();
                List<ExamResultEntity> exResult = ExamResultEntity.find("exam = ?1", exE).list();
                List<ExamAccessEntity> exAccess = ExamAccessEntity.find("exam = ?1", exE).list();
                examResponse.add(new ResponseExamDto(exE.id_exam, exE.kode_exam, exE.type_exam, exE.title_exam,
                        exE.desc_exam, exE.date_exam, exE.duration_exam, exE.status_exam, exE.limit_score_exam,
                        exResult, exQuest, exAccess));
                // examResult.add(new ResponseExamResultDto(null, null, null, null, null, null,
                // null, null, null, examResult, exam))
            }
            return Response.ok().entity(ResponseHandler.ok("get exam", examResponse)).build();
        } catch (Exception e) {
            e.printStackTrace();

            throw new InternalError("kesalahan server");
        }
    }

    @GET
    @Path("/get-exam-participant")
    public Response getExamParticipant(@Context SecurityContext ctx) {
        UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {
            // List<?> result = new ArrayList<>();
            List<ExamEntity> exam = ExamEntity
                    .find("select e from ExamEntity e join e.examAccess ea where ea.role = ?1", user.role).list();
            List<ResponseExamDto> examResponse = new ArrayList<>();
            for (ExamEntity exE : exam) {
                List<ExamQuestionEntity> exQuest = ExamQuestionEntity.find("exam = ?1", exE).list();
                List<ExamResultEntity> exResult = ExamResultEntity.find("exam = ?1 AND user = ?2", exE, user).list();
                examResponse.add(new ResponseExamDto(exE.id_exam, exE.kode_exam, exE.type_exam, exE.title_exam,
                        exE.desc_exam, exE.date_exam, exE.duration_exam, exE.status_exam, exE.limit_score_exam,
                        exResult, exQuest, null));
                // examResult.add(new ResponseExamResultDto(null, null, null, null, null, null,
                // null, null, null, examResult, exam))
            }
            return Response.ok().entity(ResponseHandler.ok("get exam", examResponse)).build();
        } catch (Exception e) {
            e.printStackTrace();

            throw new InternalError("kesalahan server");
        }
    }

    @GET
    @Path("/get-exam-participant-by-id")
    public Response getExamParticipantById(@QueryParam("id") String id, @Context SecurityContext ctx) {
        UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {
            ExamEntity exE = ExamEntity.find("""
                        SELECT e
                        FROM ExamEntity e
                        JOIN e.examAccess ea
                        WHERE ea.role = ?1 AND e.id_exam = ?2
                    """, user.role, id).firstResult();
            // ResponseExamDto examResponse = new ArrayList<>();
            List<ExamQuestionEntity> exQuest = ExamQuestionEntity.find("exam = ?1", exE).list();
            List<ExamResultEntity> exResult = ExamResultEntity.find("exam = ?1 AND user = ?2", exE, user).list();
            ResponseExamDto examResponse = new ResponseExamDto(exE.id_exam, exE.kode_exam, exE.type_exam,
                    exE.title_exam, exE.desc_exam, exE.date_exam, exE.duration_exam, exE.status_exam,
                    exE.limit_score_exam, exResult, exQuest, null);
            return Response.ok().entity(ResponseHandler.ok("get exam", examResponse)).build();
        } catch (Exception e) {
            e.printStackTrace();

            throw new InternalError("kesalahan server");
        }
    }

    @GET
    @Path("/get-exam-result-participant-by-id")
    public Response getExamResultParticipantById(@QueryParam("id") String id, @Context SecurityContext ctx) {
        UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {
            ExamResultEntity examRes = ExamResultEntity.find("user = ?1 AND id_exam_result = ?2", user, id)
                    .firstResult();
            List<ExamQuestionEntity> examQuest = ExamQuestionEntity.find("exam = ?1", examRes.exam).list();

            return Response.ok()
                    .entity(ResponseHandler.ok("get exam", new ResponseExamResultDto(examRes.id_exam_result,
                            examRes.status_pass, examRes.date_submit_exam, examRes.score, examRes.duration_result,
                            examRes.correct_answer, examRes.wrong_answer, examRes.un_answer,
                            new ResponseExamDto(examRes.exam.id_exam, examRes.exam.kode_exam, examRes.exam.type_exam,
                                    examRes.exam.title_exam, examRes.exam.desc_exam, examRes.exam.date_exam,
                                    examRes.exam.duration_exam, examRes.exam.status_exam, examRes.exam.limit_score_exam,
                                    null, null, null),
                            examRes.examQuestionResult, examQuest)))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();

            throw new InternalError("kesalahan server");
        }
    }

    // get-exam-result-participant

    @POST
    @Path("/create-exam-result")
    @Transactional
    public Response createExamResult(@Valid @RequestBody CreateExamResultDto create, @Context SecurityContext ctx) {

        Integer score_semua = 0;
        Integer correct_answer = 0;
        Integer wrong = 0;

        UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        ExamEntity exam = ExamEntity.findById(create.id_exam);

        if (create.result_answer.size() > 0) {
            for (int i = 0; i < create.result_answer.size(); i++) {
                String[] res = create.result_answer.get(i).split("\\|");
                ExamQuestionEntity exQ = ExamQuestionEntity.findById(res[0]);
                // System.out.println(res[0]);
                if (res.length >= 2 && res[1].equalsIgnoreCase(exQ.correct_answer)) {
                    score_semua += exQ.score;
                }
            }
        }

        if (create.result_answer != null && !create.result_answer.isEmpty()) {

            ExamEntity exE = ExamEntity.findById(create.id_exam);
            List<ExamQuestionEntity> exQuest = ExamQuestionEntity.find("exam = ?1", exE).list();

            Map<String, String> correctMap = new HashMap<>();

            // build questionId -> correctAnswer map
            for (ExamQuestionEntity q : exQuest) {
                correctMap.put(String.valueOf(q.id_question_exam), q.correct_answer);
            }

            for (String a : create.result_answer) {

                String[] res = a.split("\\|");

                if (res.length < 2)
                    continue;

                String questionId = res[0];
                String userAnswer = res[1];

                String correct = correctMap.get(questionId);

                if (correct != null && userAnswer.equalsIgnoreCase(correct)) {
                    correct_answer++;
                } else {
                    wrong++;
                }
            }

        }
        String key = "exam_session:"
                + user.id_user
                + ":"
                + create.id_exam;

        redis.key().del(key);

        try {

            ExamResultEntity ex = new ExamResultEntity();
            ex.date_submit_exam = LocalDateTime.now();
            ex.exam = exam;
            ex.user = user;
            ex.score = score_semua;
            ex.correct_answer = correct_answer;
            ex.wrong_answer = wrong;
            ex.duration_result = 0;
            ex.un_answer = exam.examQuestion.size() - wrong - correct_answer;
            ex.status_pass = (score_semua > exam.limit_score_exam) ? "Passed" : "Not Passed";
            ex.persist();
            if (create.result_answer.size() > 0) {
                for (int i = 0; i < create.result_answer.size(); i++) {
                    String[] res = create.result_answer.get(i).split("\\|");
                    ExamQuestionEntity exQ = ExamQuestionEntity.findById(res[0]);
                    ExamResultQuestionEntity exQR = new ExamResultQuestionEntity();
                    exQR.QuestionExam = exQ;
                    exQR.answer = res[1];
                    exQR.score_question = (res[1].equalsIgnoreCase(exQ.correct_answer)) ? exQ.score : 0;
                    exQR.examResult = ex;
                    exQR.persist();
                }
            }

            return Response.ok().entity(ResponseHandler.ok("create exam Result berhasil", ex)).build();
        } catch (Exception e) {
            e.printStackTrace();

            throw new InternalError("kesalahan server");
        }
    }

    @DELETE
    @Path("/delete-exam")
    @Transactional
    public Response deleteExam(@QueryParam("id") String id) {
        try {
            ExamEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("delete exam", null)).build();
        } catch (Exception e) {
            e.printStackTrace();

            throw new InternalError("kesalahan server");
        }
    }

    @POST
    @Path("/create-exam")
    @Transactional
    public Response createExam(@Valid @RequestBody CreateExamDto create) {
        try {
            ExamEntity ex = new ExamEntity();
            ex.kode_exam = create.kode_exam;
            ex.type_exam = create.type_exam;
            ex.title_exam = create.title_exam;
            ex.desc_exam = create.desc_exam;
            ex.duration_exam = create.duration_exam;
            ex.date_exam = create.date_exam;
            ex.limit_score_exam = create.limit_score_exam;
            ex.status_exam = create.status_exam;
            ex.persist();
            for (int i = 0; i < create.question.size(); i++) {
                ExamQuestionEntity exQ = new ExamQuestionEntity();
                exQ.exam = ex;
                exQ.question = create.question.get(i);
                exQ.question_option = create.question_option.get(i);
                exQ.question_type = create.question_type.get(i);
                exQ.correct_answer = create.correct_answer.get(i);
                exQ.score = create.score.get(i);
                exQ.persist();
            }

            for (int e = 0; e < create.role_access.size(); e++) {
                RoleEntity re = RoleEntity.findById(create.role_access.get(e));
                ExamAccessEntity eA = new ExamAccessEntity();
                eA.exam = ex;
                eA.role = re;
                eA.persist();
            }
            return Response.ok().entity(ResponseHandler.ok("create exam", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("kesalahan server");
        }
    }

    @POST
    @Path("/edit-exam")
    @Transactional
    public Response editExam(@Valid @RequestBody CreateExamDto create) {
        try {
            ExamEntity ex = ExamEntity.findById(create.id_exam);
            ex.kode_exam = create.kode_exam;
            ex.type_exam = create.type_exam;
            ex.title_exam = create.title_exam;
            ex.desc_exam = create.desc_exam;
            ex.duration_exam = create.duration_exam;
            ex.date_exam = create.date_exam;
            ex.limit_score_exam = create.limit_score_exam;
            ex.status_exam = create.status_exam;
            // ex.persist();
            if (create.question.size() > 0) {
                Long deleteData = ExamQuestionEntity.delete("exam = ?1", ex);
                if (deleteData > 0) {
                    for (int i = 0; i < create.question.size(); i++) {

                        ExamQuestionEntity exQ = new ExamQuestionEntity();
                        exQ.exam = ex;
                        exQ.question = create.question.get(i);
                        exQ.question_option = create.question_option.get(i);
                        exQ.question_type = create.question_type.get(i);
                        exQ.correct_answer = create.correct_answer.get(i);
                        exQ.score = create.score.get(i);
                        exQ.persist();
                    }

                }

            }
            if (create.role_access.size() > 0) {
                ExamAccessEntity.delete("exam = ?1", ex);

                for (int e = 0; e < create.role_access.size(); e++) {
                    RoleEntity re = RoleEntity.findById(create.role_access.get(e));
                    ExamAccessEntity eA = new ExamAccessEntity();
                    eA.exam = ex;
                    eA.role = re;
                    eA.persist();
                }
            }

            return Response.ok().entity(ResponseHandler.ok("Edit exam", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("kesalahan server");
        }
    }

    @POST
    @Path("/create-exam-session")
    @Transactional
    public Response createExamSession(@QueryParam("id") String id, @Context SecurityContext ctx,
            @QueryParam("duration") String duration) {
        UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();

        try {
            ValueCommands<String, String> value = redis.value(String.class);

            String key = "exam_session:" + user.id_user + ":" + id;
            int minutes = Integer.parseInt(duration);

            int seconds = minutes * 60;

            // CHECK EXISTING SESSION
            String existingSession = value.get(key);

            if (existingSession != null) {

                long ttl = redis.key().ttl(key);

                JsonObject existing = new JsonObject(
                        existingSession);

                existing.put(
                        "remainingSeconds",
                        ttl);

                return Response.ok()
                        .entity(
                                ResponseHandler.ok(
                                        "Session already exists",
                                        existing))
                        .build();
            }

            JsonObject session = new JsonObject()
                    .put("userId", user.id_user)
                    .put("examId", id)
                    .put("status", "ONGOING")
                    .put("startedAt", LocalDateTime.now().toString())
                    .put("expiredAt",
                            LocalDateTime.now().plusHours(2).toString());

            value.setex(
                    key,
                    seconds,
                    session.encode());
            return Response.ok().entity(ResponseHandler.ok("Success create session", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Kesalahan Server");
            // TODO: handle exception
        }

    }

    @GET
    @Path("/check-session")
    public Response checkSession(
            @QueryParam("id") String id,
            @Context SecurityContext ctx) {

        UserEntity user = UserEntity.find(
                "email = ?1",
                ctx.getUserPrincipal()
                        .getName())
                .firstResult();

        ValueCommands<String, String> value = redis.value(String.class);

        String key = "exam_session:"
                + user.id_user
                + ":"
                + id;

        String session = value.get(key);

        // SESSION EXPIRED
        if (session == null) {

            JsonObject data = new JsonObject();

            data.put(
                    "active",
                    false);

            data.put(
                    "remainingSeconds",
                    0);

            return Response.ok()
                    .entity(
                            ResponseHandler.ok(
                                    "Session expired",
                                    data))
                    .build();
        }

        // SESSION NOT FOUND

        long redisTTl = redis.key().ttl(key);
        // GET REMAINING TTL
        Duration ttl = Duration.ofSeconds(redisTTl);

        JsonObject data = new JsonObject(session);

        data.put(
                "remainingSeconds",
                ttl.getSeconds());
        data.put(
                "active",
                true);

        return Response.ok()
                .entity(
                        ResponseHandler.ok(
                                "Session active",
                                data))
                .build();
    }

}
