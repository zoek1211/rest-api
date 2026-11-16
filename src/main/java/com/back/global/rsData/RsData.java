package com.back.global.rsData;

/**
 * 조회가 아닌 요청(삭제, 작성, 수정 ...)의 응답을 하나의 형태로 통일하는 "응답 봉투(Result Data)"입니다.
 *
 * <pre>
 * {
 *   "resultCode": "200-1",
 *   "msg": "2번 댓글이 삭제되었습니다.",
 *   "data": null
 * }
 * </pre>
 *
 * <ul>
 *   <li><b>resultCode</b>: {@code HTTP상태코드-세부코드} 형식. 예) {@code 200-1}은 "성공(200) 중 1번 상황".
 *       같은 200이라도 상황을 구분하고 싶을 때 뒤의 숫자로 나눈다.</li>
 *   <li><b>msg</b>: 사람이 읽을 메시지. 프론트는 이걸 그대로 토스트/알림창에 띄울 수 있다.</li>
 *   <li><b>data</b>: resultCode와 msg만으로 부족한 추가 데이터(예: 새로 작성된 글의 DTO).</li>
 * </ul>
 *
 * <p><b>제네릭 {@code <T>}</b>
 * <br>data에 어떤 타입이든 담을 수 있게 합니다. {@code RsData<PostDto>}면 data는 PostDto,
 * 삭제처럼 담을 게 없으면 {@code RsData<Void>}로 "data는 비어 있다"는 의도를 타입으로 드러냅니다.
 * (그냥 {@code RsData}라고 쓰면 {@code RsData<Object>}처럼 동작하지만, 의도가 안 보여서 지양합니다.)
 *
 * <p><b>조회 API에는 쓰지 않습니다.</b> 조회 성공은 대부분 실패할 일이 없어서
 * DTO를 그대로 응답하는 편이 단순합니다(쓰면 틀린 건 아니지만 오버스펙).
 */
public record RsData<T>(String resultCode, String msg, T data) {
    /** data가 없는 응답용 보조 생성자. data는 null로 채워진다 → JSON에서 {@code "data": null} */
    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }
}
