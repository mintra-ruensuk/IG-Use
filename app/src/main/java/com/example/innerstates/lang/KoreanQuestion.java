package com.example.innerstates.lang;

import com.example.innerstates.Choice;

public class KoreanQuestion {
    // "What best describes how you used Instagram just now for communication?"
    public static String type_of_communication = "다음 중 당신이 인스타그램을 의사소통을 위해 어떻게 사용했는지, 가장 잘 설명하는 것은 무엇입니까??";

    // I was paying a lot of attention to how I do things compared to how people I follow on Instagram do things
    public static String social1 = "나는 인스타그램에서 팔로우 하는 사람들이 하는 것들 보다, 내가 하는 것들에 많은 신경을 쓰고 있다.";

    // I was wanting to find out how well I do things compared to people I follow on Instagram
    public static String social2 = "나는 인스타그램에서 팔로우하는 사람들과 비교해서 내가 얼마나 잘 하고 있는지 알고 싶다.";

    // I generally feel inferior to others
    public static String envy1 = "나는 일반적으로 남들보다 열등하다고 느낀다.";

    // It is so frustrating to see some people I follow on Instagram always having a good time
    public static String envy2 = "내가 인스타그램에서 팔로우하는 사람들이 항상 즐거운 시간을 보내는 것을 보면 너무 좌절스럽다";

    // Original: It somehow doesn’t seem fair that some people I follow on Instagram seem to have all the fun
    public static String envy3 = "내가 인스타그램에서 팔로우하는 사람들이 모든 즐거움을 다 가지고 있는 것처럼 보이는 것은 불공평한 것 같다.";

    // I wish I can travel as much as some of my friends on Instagram do
    public static String envy4 = "인스타그램에 있는 친구들만큼 나도 여행을 많이 다닐 수 있으면 좋겠다.";

    // Many of my friends on Instagram have a better life than me
    public static String envy5 = "인스타그램에 있는 많은 친구들이 나보다 더 나은 삶을 살고 있다.";

    // Many of my friends on Instagram are happier than me
    public static String envy6 = "인스타그램에 있는 많은 친구들이 나보다 더 행복하다.";


    // On the whole, I am satisfied with myself.
    public static String esteem1 = "나는 나 자신에 대해 괜찮게 생각한다.";

    // I am able to do things as well as most other people I follow on Instagram.
    public static String esteem2 = "나는 내가 인스타그램에서 팔로우 하는 대부분의 사람들만큼 일을 해낼 수 있다.";

    // I feel I do not have much to be proud of
    public static String esteem3 = "내게는 자랑으로 여길만한 것이 별로 없다";

    // From the last time we ask, how often have you been bothered by the following problems?
    public static String depress1 = "이전 설문 응답 후 지금까지, 당신은 다음과 같은 일로 얼마나 자주 불편함을 느낀 적이 있습니까?";

    // Little interest or pleasure in doing things
    public static String depress2 = "일을 함에 있어 거의 흥미가 없거나 즐거움이 없다";

    // Feeling down, depressed or hopeless
    public static String depress3 = "기분이 가라앉거나 우울하거나 희망이 없다";

    // When I was viewing posts on Instagram, I focus on their (body) appearance
    public static String body1 = "인스타그램에서 게시물을 볼 때, 나는 그들의 (신체, 몸) 외모에 집중한다.";

    // When I was viewing posts on Instagram, I compared their overall (body) appearance to my (body) appearance
    public static String body2 = "인스타그램에서 게시물을 볼 때, 나는 그들의 전반적 (신체, 몸) 외모를 나의 (신체, 몸) 외모와 비교했다.";

    // How do you feel now?
    public static String sameText = "지금 기분이 어떠세요?";

    // Why do you think you were feeling this way?
    public static String openQ1 = "왜 이렇게 느꼈다고 생각하십니까?";

    // Describe a few examples of posts or actions (e.g., push like, Direct message, etc.,) you did that make you feel that way.
    public static String openQ2 = "당신이 그렇게 느끼도록 만든 게시물의 예시 또는 인스타그램 내에서의 행동(예시: 좋아요 누르기, 메시지 보내기 등)을 설명해주세요.";




    public static Choice[] choiceCommunication() {
        // For direct communication with others, For consuming information without direct communication with others
        Choice[] choices = new Choice[2];
        choices[0] = new Choice("타인과의 직접적인 교류를 위해", 1);
        choices[1] = new Choice("직접적인 교류없이 정보를 소비하기 위해", 2);
        return choices;
    }

    public static Choice[] disToAgree() {
        // I strongly disagree ---> I strongly agree
        Choice[] choices = new Choice[5];
        choices[0] = new Choice("매우 동의하지 않는다", 1);
        choices[1] = new Choice("동의하지 않는다", 2);
        choices[2] = new Choice("동의도 부정도 하지 않는다 ", 3);
        choices[3] = new Choice("동의한다", 4);
        choices[4] = new Choice("매우 동의한다", 5);
        return choices;
    }

    public static Choice[] esteemScale() {
        // I strongly disagree ---> I strongly agree
        Choice[] choices = new Choice[4];
        choices[0] = new Choice("매우 동의하지 않는다", 1);
        choices[1] = new Choice("동의하지 않는다", 2);
        choices[2] = new Choice("동의한다", 3);
        choices[3] = new Choice("매우 동의한다", 4);
        return choices;
    }

    public static Choice[] depressScale() {

        Choice[] choices = new Choice[4];
        choices[0] = new Choice("전혀 그렇지 않았다", 1);
        choices[1] = new Choice("몇 번 그런적이 있다", 2);
        choices[2] = new Choice("절반 이상 그랬다", 3);
        choices[3] = new Choice("거의 항상 그랬다", 4);
        return choices;
    }

    public static Choice[] bodyScale() {

        Choice[] choices = new Choice[5];
        choices[0] = new Choice("전혀 그렇지 않았다", 1);
        choices[1] = new Choice("", 2);
        choices[2] = new Choice("", 3);
        choices[3] = new Choice("", 4);
        choices[4] = new Choice("거의 항상 그랬다", 5);
        return choices;
    }

    public static Choice[] samScale() {

        Choice[] choices = new Choice[9];
        choices[0] = new Choice("", 1);
        choices[1] = new Choice("", 2);
        choices[2] = new Choice("", 3);
        choices[3] = new Choice("", 4);
        choices[4] = new Choice("", 5);
        choices[5] = new Choice("", 6);
        choices[6] = new Choice("", 7);
        choices[7] = new Choice("", 8);
        choices[8] = new Choice("", 9);
        return choices;
    }
}
