export const occasionalTypeInfoMapperHelper = (occasionalType) => {
    switch(occasionalType) {
        case 'D2_BIRTH':
            return 'Dwudniowy urlop okolicznościowy przysługuje w związku z narodzinami dziecka.';
        case 'D2_FUNERAL':
            return 'Dwudniowy urlop okolicznościowy przysługuje w związku z zgonem i pogrzebem małżonka, dziecka, ojca, matki, ojczyma lub macochy.';
        case 'D2_WEDDING':
            return 'Dwudniowy urlop okolicznościowy przysługuje w związku z twoim ślubem.';
        case 'D1_FUNERAL':
            return 'Jednodniowy urlop okolicznościowy przysługuje w związku z zgonem i pogrzebem siostry, brata, teściowej, teścia, babki, dziadka, a także innej osoby, którą utrzymujesz lub którą się bezpośrednio opiekujesz.';
        case 'D1_WEDDING':
            return 'Jednodniowy urlop okolicznościowy przysługuje w związku z ślubem Twojego dziecka.';
        default:
            return '';
    }
}