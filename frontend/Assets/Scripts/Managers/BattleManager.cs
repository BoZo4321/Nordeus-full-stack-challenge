using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class BattleManager : MonoBehaviour
{
    [Header("Combatants - drag CombatantUI components here")]
    [SerializeField] private CombatantUI heroUI;
    [SerializeField] private CombatantUI monsterUI;

    [Header("Move Buttons - drag 4 MoveButtonUI components here")]
    [SerializeField] private MoveButtonUI[] moveButtonUIs;

    [Header("Battle Log")]
    [SerializeField] private Text battleLogText;
    [SerializeField] private ScrollRect battleLogScroll;

    [Header("Post Battle Panel")]
    [SerializeField] private GameObject postBattlePanel;
    [SerializeField] private Text postBattleResultText;
    [SerializeField] private Text postBattleDetailsText;
    [SerializeField] private Button continueButton;
    [SerializeField] private Button retryButton;
    [SerializeField] private Button backToMapButton;

    private StartBattleResponse _battle;
    private List<string> _log = new();
    private bool _battleOver;

    void Start()
    {
        _battle = GameManager.Instance.CurrentBattle;
        postBattlePanel.SetActive(false);

        heroUI.Setup(_battle.hero.name, _battle.hero.currentHealth, _battle.hero.maxHealth);
        monsterUI.Setup(_battle.monster.name, _battle.monster.currentHealth, _battle.monster.maxHealth);

        SetupMoveButtons(_battle.hero.moves);
        AppendLog("Battle started against " + _battle.monster.name + "!");
    }

    private void SetupMoveButtons(Move[] moves)
    {
        for (int i = 0; i < moveButtonUIs.Length; i++)
        {
            if (i < moves.Length)
                moveButtonUIs[i].Setup(moves[i], PlayTurn);
            else
                moveButtonUIs[i].Setup(new Move { name = "-" }, null);
        }
    }

    private void PlayTurn(string moveId)
    {
        if (_battleOver) return;
        SetMovesInteractable(false);

        StartCoroutine(ApiClient.Instance.Post<PlayerTurnResponse>(
            "/battles/" + GameManager.Instance.CurrentBattleId + "/turns",
            new PlayerTurnRequest { moveId = moveId },
            OnTurnResponse,
            error =>
            {
                AppendLog("Error: " + error);
                SetMovesInteractable(true);
            }
        ));
    }

        private void OnTurnResponse(PlayerTurnResponse response)
    {
        heroUI.Refresh(response.hero.currentHealth, response.hero.maxHealth);
        heroUI.ShowEffects(response.hero.activeEffects);

        monsterUI.Refresh(response.monster.currentHealth, response.monster.maxHealth);
        monsterUI.ShowEffects(response.monster.activeEffects);

        if (response.playerMove != null) AppendLog(response.playerMove.message);
        if (response.monsterMove != null) AppendLog(response.monsterMove.message);

        if (response.battleStatus == "HERO_WON")
        {
            _battleOver = true;
            ShowPostBattle("Victory!", BuildRewardText(response.reward), won: true);
        }
        else if (response.battleStatus == "HERO_LOST")
        {
            _battleOver = true;
            ShowPostBattle("Defeated", "You can retry this fight.", won: false);
        }
        else
        {
            SetMovesInteractable(true);
        }
    }

    private string BuildRewardText(BattleReward reward)
    {
        if (reward == null) return "";
        var sb = new System.Text.StringBuilder();
        sb.Append("+" + reward.xpAwarded + " XP");
        if (reward.leveledUp) sb.Append("  |  LEVEL UP!");
        if (reward.learnedMove != null) sb.Append("\nLearned: " + reward.learnedMove.name + "!");
        return sb.ToString();
    }

    private void ShowPostBattle(string result, string details, bool won)
    {
        postBattlePanel.SetActive(true);
        postBattleResultText.text = result;
        postBattleDetailsText.text = details;
        continueButton.gameObject.SetActive(won);
        retryButton.gameObject.SetActive(!won);
        backToMapButton.gameObject.SetActive(!won);

        continueButton.onClick.RemoveAllListeners();
        continueButton.onClick.AddListener(ReturnToMap);
        retryButton.onClick.RemoveAllListeners();
        retryButton.onClick.AddListener(RetryBattle);

        backToMapButton.onClick.RemoveAllListeners();
        backToMapButton.onClick.AddListener(ReturnToMap);
    }

    private void RetryBattle()
    {
        retryButton.interactable = false;
        StartCoroutine(ApiClient.Instance.Post<StartBattleResponse>(
            "/runs/" + _battle.runId + "/battles",
            new StartBattleRequest { monsterId = _battle.monster.id },
            response =>
            {
                GameManager.Instance.CurrentBattleId = response.battleId;
                GameManager.Instance.CurrentBattle = response;
                SceneManager.LoadScene("BattleScene");
            },
            error =>
            {
                AppendLog("Error: " + error);
                retryButton.interactable = true;
            }
        ));
    }

    private void ReturnToMap()
    {
        StartCoroutine(ApiClient.Instance.Get<StartRunResponse>(
            "/runs/" + GameManager.Instance.CurrentRunId,
            run =>
            {
                GameManager.Instance.CurrentRun = run;
                SceneManager.LoadScene("MapScene");
            }
        ));
    }

    private void AppendLog(string line)
    {
        if (string.IsNullOrEmpty(line)) return;
        _log.Add(line);
        if (_log.Count > 20) _log.RemoveAt(0);
        battleLogText.text = string.Join("\n", _log);
        Canvas.ForceUpdateCanvases();
        if (battleLogScroll != null) battleLogScroll.verticalNormalizedPosition = 0f;
    }

    private void SetMovesInteractable(bool value)
    {
        foreach (var btn in moveButtonUIs) btn.SetInteractable(value);
    }
}